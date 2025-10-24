package execution.controller;

import com.google.gson.Gson;
import dto.ArchitectureGeneration;
import dto.ProgramData;
import jakarta.servlet.http.HttpServletResponse;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import execution.model.InstructionTableEntry;
import dto.InstructionDTO;
import dto.Searchable;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static configuration.ClientConfiguration.CLIENT;
import static configuration.DialogUtils.showAlert;
import static configuration.ResourcesConfiguration.*;

public class LeftSideController {

    private PrimaryController primaryController;
    private RightSideController rightController;
    private TopComponentController topController;
    private IntegerProperty maxLevel = new SimpleIntegerProperty(-1);
    private final IntegerProperty insufficientArchitectureCount = new SimpleIntegerProperty(0);
    private final int HISTORY_CHAIN_EFFECT_DURATION = 300; // milliseconds

    @FXML
    private TableView<InstructionTableEntry> chosenInstructionHistoryTable;

    @FXML
    private MenuButton expansionLevelMenu;

    @FXML
    private MenuButton highlightSelection;

    @FXML
    private TableView<InstructionTableEntry> instructionsTable;

    @FXML
    private Button clearBreakpointsBtn;

    @FXML
    private Label summaryLine;

    @FXML
    private Label maxExpandLevel;

    @FXML
    public void initialize() {

        instructionsTable.setRowFactory(tv -> new TableRow<>() {
            private boolean currentlyInsufficient = false;

            @Override
            protected void updateItem(InstructionTableEntry item, boolean empty) {
                super.updateItem(item, empty);
                boolean shouldBeInsufficient = false;
                getStyleClass().removeAll("insufficient-architecture-row");

                if (item != null && !empty) {
                    if (rightController != null) {
                        if (!isArchitectureEnough(rightController.getSelectedArchitecture(), item.getArchitecture())) {
                            getStyleClass().add("insufficient-architecture-row");
                            shouldBeInsufficient = true;
                        } else {
                            getStyleClass().remove("insufficient-architecture-row");
                        }
                    }
                }

                if (shouldBeInsufficient != currentlyInsufficient) {
                    if (shouldBeInsufficient) { // Was sufficient, now insufficient
                        insufficientArchitectureCount.set(insufficientArchitectureCount.get() + 1);
                    } else { // Was insufficient, now sufficient
                        insufficientArchitectureCount.set(insufficientArchitectureCount.get() - 1);
                    }
                    currentlyInsufficient = shouldBeInsufficient;
                }

                this.setOnMouseClicked(event -> {
                    if (!this.isEmpty()) {
                        updateParentInstructionTable(this.getItem().getInstructionDTO());
                    }
                });
            }
    });

        // Initialize the menu button of the expansion levels
        expansionLevelMenu.getItems().clear();
        maxLevel.addListener((obs, oldValue, newValue) -> {
            updateAvailableExpansionLevels(newValue.intValue());
        });

        // Initialize the highlight selection menu button
        highlightSelection.getItems().clear();

        // Initialize the history chain table and disable row selection
        chosenInstructionHistoryTable.setSelectionModel(null);

        instructionsTable.getSortOrder().clear();
        instructionsTable.setSortPolicy(param -> null); // Disables sorting globally
        chosenInstructionHistoryTable.getSortOrder().clear();
        chosenInstructionHistoryTable.setSortPolicy(param -> null); // Disables sorting globally

        // Minimize tables' height to prevent vertical scrolling
        instructionsTable.setPrefHeight(instructionsTable.getPrefHeight() * 0.85);
        chosenInstructionHistoryTable.setPrefHeight(chosenInstructionHistoryTable.getPrefHeight() * 0.85);

        // Align summary line to center
        summaryLine.setTextAlignment(TextAlignment.CENTER);
    }

    @FXML
    public void clearAllBreakpoints(ActionEvent event) {
        sendDeleteAllBreakpointsRequest();
    }

    public void setPrimaryController(PrimaryController primaryController) {
        this.primaryController = primaryController;

        // Initialize the max label
        maxExpandLevel.textProperty().bind(
                Bindings.when(
                                Bindings.createBooleanBinding(
                                        () -> primaryController.program != null,
                                        maxLevel
                                )
                        )
                        .then(Bindings.createStringBinding(() -> "/" + maxLevel.get(), maxLevel))
                        .otherwise("/Max")
        );

        summaryLine.textProperty().bind(
                Bindings.when(Bindings.createBooleanBinding(
                                () -> primaryController.program != null,
                                instructionsTable.getItems()
                        ))
                        .then(Bindings.createStringBinding(() ->
                                        "Program contains " +
                                                instructionsTable.getItems().stream().filter(entry -> "B".equals(entry.getType())).count() +
                                                " Basic Instructions, " +
                                                instructionsTable.getItems().stream().filter(entry -> "S".equals(entry.getType())).count() +
                                                " Synthetic Instructions," +
                                                "\n" +
                                                instructionsTable.getItems().stream().filter(entry -> ArchitectureGeneration.IV.toString().equals(entry.getArchitecture())).count() +
                                                " Instructions in Architecture IV, " +
                                                instructionsTable.getItems().stream().filter(entry -> ArchitectureGeneration.III.toString().equals(entry.getArchitecture())).count() +
                                                " Instructions in Architecture III, " +
                                                instructionsTable.getItems().stream().filter(entry -> ArchitectureGeneration.II.toString().equals(entry.getArchitecture())).count() +
                                                " Instructions in Architecture II and " +
                                                instructionsTable.getItems().stream().filter(entry -> ArchitectureGeneration.I.toString().equals(entry.getArchitecture())).count() +
                                                " Instructions in Architecture I." ,
                                instructionsTable.getItems()
                        ))
                        .otherwise("No program loaded.")
        );

        chosenInstructionHistoryTable.placeholderProperty().bind(
                Bindings.createObjectBinding(() -> {
                    if (primaryController.program == null) {
                        return new Label("No program loaded.");
                    } else if (instructionsTable.getSelectionModel().getSelectedItem() == null) {
                        return new Label("Choose an instruction from the table above to present its history.");
                    } else {
                        return new Label("This instruction has no history");
                    }
                }, new SimpleObjectProperty<>(primaryController.program), instructionsTable.getSelectionModel().selectedItemProperty())
        );
    }

    private void updateParentInstructionTable(InstructionDTO instruction) {
        List<InstructionTableEntry> historyEntries = new ArrayList<>();
        InstructionDTO parent = instruction.getParentInstruction();
        while (parent != null) {
            historyEntries.add(new InstructionTableEntry(parent));
            parent = parent.getParentInstruction();
        }

        // Clear the table first
        chosenInstructionHistoryTable.getItems().clear();
        chosenInstructionHistoryTable.getItems().addAll(historyEntries);

        if (topController.isAnimationAllowedProperty().get()) { // Case: animation allowed
            playHistoryChainAnimation();
        } else { // Case: user requests no animations
            List<Node> rows = new ArrayList<>(chosenInstructionHistoryTable.lookupAll(".table-row-cell"));
            rows.forEach(row -> row.setOpacity(1));
        }
    }

    private void playHistoryChainAnimation() {
        List<Node> rows = new ArrayList<>(chosenInstructionHistoryTable.lookupAll(".table-row-cell"));
        rows.forEach(row -> row.setOpacity(0));
        int delay = 0;
        for (Node row : rows) {
            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            pause.setOnFinished(e -> {
                FadeTransition fade = new FadeTransition(Duration.millis(200), row);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
            });
            pause.play();
            delay += HISTORY_CHAIN_EFFECT_DURATION;
        }
    }

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;

        highlightSelection.disableProperty().bind(
                Bindings.isEmpty(highlightSelection.getItems())
                        .or(rightController.isInDebugModeProperty())
        );

        expansionLevelMenu.disableProperty().bind(
                Bindings.isEmpty(expansionLevelMenu.getItems())
                        .or(rightController.isInDebugModeProperty())
        );

        // Bind the selection of an instruction in the left table to "if in debug mode" in right controller
        instructionsTable.addEventFilter(MouseEvent.ANY, event -> {
            if (rightController.isInDebugModeProperty().get()) {
                // Allow interaction only if the event target is a TableCell in the id column
                Node target = event.getTarget() instanceof Node ? (Node) event.getTarget() : null;
                while (target != null && !(target instanceof TableCell)) {
                    target = target.getParent();
                }
                if (target instanceof TableCell<?, ?> cell) {
                    TableColumn<?, ?> column = cell.getTableColumn();
                    if ("idColumn".equals(column.getId())) {
                        return; // Allow event for id column
                    }
                }
                event.consume(); // Block all other interactions
            }
        });

        // Set red circles implementation on idColumn for breakpoints
        instructionsTable.getColumns().stream()
                .filter(column -> "idColumn".equals(column.getId()))
                .findFirst()
                .ifPresent(column -> {
                    TableColumn<InstructionTableEntry, Number> idColumn = (TableColumn<InstructionTableEntry, Number>) column;
                    idColumn.setCellFactory(col -> new TableCell<>() {
                        private final Circle circle = new Circle(6);
                        private final StackPane wrapper = new StackPane(circle);
                        private boolean isActive = false;
                        {
                            setStyle("-fx-alignment: center;");
                            wrapper.setAlignment(Pos.CENTER);
                            circle.setFill(Color.rgb(217, 83, 79, 1.0));
                            circle.setStroke(Color.rgb(139, 43, 43));

                            // Click handler: toggle breakpoint
                            this.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                                if (getItem() == null) return;
                                isActive = !isActive;
                                InstructionTableEntry entry = getTableRow().getItem();
                                if (isActive) {
                                    sendAddBreakpointRequest(this.getItem().intValue());
                                    setText(null);
                                    setGraphic(wrapper);
                                    circle.setFill(Color.rgb(217, 83, 79, 1.0));
                                    if (entry != null) entry.setBreakpoint(true);
                                } else {
                                    sendDeleteBreakpointRequest(this.getItem().intValue()); // Remove any existing breakpoint first to avoid duplicates
                                    setText(getItem().toString());
                                    setGraphic(null);
                                    if (entry != null) entry.setBreakpoint(false);
                                }
                                e.consume(); // Prevent the event from propagating to the row selection
                            });

                            // Hover handlers
                            this.setOnMouseEntered(e -> {
                                if (!isActive && getItem() != null) {
                                    setText(null);
                                    setGraphic(wrapper);
                                    circle.setFill(Color.rgb(217, 83, 79, 0.5));
                                }
                            });
                            this.setOnMouseExited(e -> {
                                if (!isActive && getItem() != null) {
                                    setText(getItem().toString());
                                    setGraphic(null);
                                }
                            });
                        }

                        @Override
                        protected void updateItem(Number item, boolean empty) {
                            super.updateItem(item, empty);

                            if (empty || item == null) {
                                setText(null);
                                setGraphic(null);
                                isActive = false;
                                return;
                            }

                            InstructionTableEntry entry = getTableRow().getItem();
                            if (entry != null && entry.isBreakpoint()) {
                                sendAddBreakpointRequest(entry.getId());
                                isActive = true;
                                setText(null);
                                setGraphic(wrapper);
                                circle.setFill(Color.rgb(217, 83, 79, 1.0));
                            } else {
                                if (entry != null) {
                                    sendDeleteBreakpointRequest(entry.getId());
                                }
                                isActive = false;
                                setText(item.toString());
                                setGraphic(null);
                            }
                        }
                    });
                });

//        rightController.getSelectedArchitectureProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue == null) {
//                return;
//            }
//
//            instructionsTable.getItems().forEach(entry -> {
//                if (!isArchitectureEnough(newValue, entry.getArchitecture())) {
//                    entry.getClass().getStyleClass().add("insufficient-architecture-row");
//                } else {
//                    entry.setDisabled(true);
//                }
//            })
//      });
            Platform.runLater(() -> instructionsTable.refresh());

    }

    public void updateMainInstructionTable() {
        List<InstructionTableEntry> entries = primaryController.program.getProgramInstructions().stream()
                .map(InstructionTableEntry::new) // Convert InstructionDTO -> InstructionTableEntry
                .toList();
        Platform.runLater(() -> instructionsTable.getItems().setAll(entries)); // Replace items in the table
        sendDeleteAllBreakpointsRequest(); // Clear all breakpoints when loading a new function, a new program or when changing the expansion level
    }

    public void setCurrentLevel(int level) {
        Platform.runLater(() -> expansionLevelMenu.setText(String.valueOf(level)));
    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void markEntryInInstructionTable(int entryId) {
        Platform.runLater(() -> {
            instructionsTable.getSelectionModel().clearAndSelect(entryId);
            instructionsTable.getFocusModel().focus(entryId);
            instructionsTable.scrollTo(entryId);
        });
    }

    public void clearMarkInInstructionTable() {
        Platform.runLater(() -> instructionsTable.getSelectionModel().clearSelection());
    }

    public void updateAvailableExpansionLevels(int maxLevel) {
        expansionLevelMenu.getItems().clear();
        IntStream.rangeClosed(0, maxLevel)
                .mapToObj(i -> {
                    Label label = new Label(String.valueOf(i));
                    label.setMaxWidth(Double.MAX_VALUE);
                    label.setStyle("-fx-alignment: center;");
                    CustomMenuItem menuItem = new CustomMenuItem(label, true);
                    menuItem.setUserData(i);
                    label.prefWidthProperty().bind(expansionLevelMenu.widthProperty());
                    menuItem.setOnAction((ActionEvent event) -> {
                        int selectedLevel = (int) menuItem.getUserData();
                        sendExpansionForActiveProgramRequest(selectedLevel);
                    });
                    return menuItem;
                })
                .forEach(expansionLevelMenu.getItems()::add);
    }

    public void updateVariablesOrLabelSelectionMenu() {
        Set<Searchable> searchables = new HashSet<>();
        highlightSelection.getItems().clear();
        instructionsTable.getItems().forEach(entry -> {
            searchables.addAll(entry.getSearchables().stream().filter(searchable -> !Objects.equals(searchable.getName(), "    ")).collect(Collectors.toSet()));
        });

        List<Searchable> sortedSearchables = searchables.stream()
                .sorted(Comparator
                        .comparingInt((Searchable s) -> {
                            String name = s.getName();
                            if (name.startsWith("y")) return 1;
                            if (name.startsWith("x")) return 2;
                            if (name.startsWith("z")) return 3;
                            if (name.startsWith("L")) return 4;
                            return 5; // everything else goes last
                        })
                        .thenComparingInt(s -> {
                            String name = s.getName().substring(1); // drop prefix
                            try {
                                return Integer.parseInt(name);
                            } catch (NumberFormatException e) {
                                return Integer.MAX_VALUE; // non-numeric goes last
                            }
                        })
                )
                .toList();

        sortedSearchables.forEach(searchable -> {
            Label label = new Label(searchable.getName());
            label.setMaxWidth(Double.MAX_VALUE);
            label.setStyle("-fx-alignment: center;");
            CustomMenuItem Choice = new CustomMenuItem(label, true);
            Choice.setUserData(searchable.getName());
            label.prefWidthProperty().bind(highlightSelection.widthProperty());
            Choice.setOnAction((ActionEvent event) -> {
                instructionsTable.getSelectionModel().clearSelection();
                instructionsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                instructionsTable.getItems().stream().filter(inst -> inst.getSearchables().stream()
                        .anyMatch(s -> s.getName().equals(searchable.getName()))
                ).forEach(inst -> {
                    instructionsTable.getSelectionModel().select(inst);
                });
            });
            highlightSelection.getItems().add(Choice);
        });
    }

    public void clearHistoryChainTable() {
        Platform.runLater(() -> chosenInstructionHistoryTable.getItems().clear());
    }

    public Set<InstructionTableEntry> getEntriesWithBreakpoints() {
        return instructionsTable.getItems().stream()
                .filter(InstructionTableEntry::isBreakpoint)
                .collect(Collectors.toSet());
    }

    public void sendAddBreakpointRequest(int breakpointRow) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + BREAKPOINT_RESOURCE))
                .newBuilder();
        String finalURL = urlBuilder.build().toString();

        // Create a temporal object containing the amount of credits to charge as json
        Gson gson = new Gson();
        String json = gson.toJson(Map.of("lineNumber", breakpointRow));

        // Build the body sent to the server to include the creditRequest object
        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(finalURL)
                .post(body)
                .build();

        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    showAlert("Failed to set a new breakpoint on line" + breakpointRow + "\n" + "Code: " + response.code(),
                            (Stage) clearBreakpointsBtn.getScene().getWindow());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showAlert("Failed to send a request to set the breakpoint on line" + breakpointRow,
                        (Stage) clearBreakpointsBtn.getScene().getWindow());
            }
        });
    }

    public void sendDeleteBreakpointRequest(int breakpointRow) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + BREAKPOINT_RESOURCE))
                .newBuilder();
        String finalURL = urlBuilder.build().toString();

        // Create a temporal object containing the amount of credits to charge as json
        Gson gson = new Gson();
        String json = gson.toJson(Map.of("lineNumber", breakpointRow));

        // Build the body sent to the server to include the creditRequest object
        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(finalURL)
                .delete(body)
                .build();

        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    showAlert("Failed to delete a breakpoint on line" + breakpointRow + "\n" + "Code: " + response.code(),
                            (Stage) clearBreakpointsBtn.getScene().getWindow());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Failed to send a request to delete the breakpoint on line" + breakpointRow,
                                (Stage) clearBreakpointsBtn.getScene().getWindow())
                );
            }
        });
    }

    public void sendExpansionForActiveProgramRequest(int level) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + EXPAND_PROGRAM_RESOURCE))
                .newBuilder()
                .addQueryParameter("level", String.valueOf(level));
        String finalURL = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(finalURL)
                .get()
                .build();

        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.code() == HttpServletResponse.SC_OK) {
                        try (ResponseBody responseBody = response.body()) {
                            Gson gson = new Gson();
                            primaryController.program = gson.fromJson(Objects.requireNonNull(responseBody).string(), ProgramData.class);
                            updateMainInstructionTable();
                            Platform.runLater(() -> {
                                updateVariablesOrLabelSelectionMenu();
                                setCurrentLevel(level);
                            });
                                rightController.updateResultVariableTable();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == HttpServletResponse.SC_NO_CONTENT) {
                        showAlert("No program data detected for the user.", (Stage) expansionLevelMenu.getScene().getWindow());
                    }
                } else {
                    showAlert("Failed to expand active program to " + level + "\n" + "Code: " + response.code(),
                            (Stage) expansionLevelMenu.getScene().getWindow());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showAlert("Failed to send a request to expand the active program to " + level,
                        (Stage) expansionLevelMenu.getScene().getWindow());
            }
        });
    }

    public void sendDeleteAllBreakpointsRequest() {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + BREAKPOINT_RESOURCE))
                .newBuilder();
        String finalURL = urlBuilder.build().toString();

        // Create a temporal object containing the amount of credits to charge as json
        Gson gson = new Gson();
        String json = gson.toJson(Map.of("lineNumber", "all"));

        // Build the body sent to the server to include the creditRequest object
        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(finalURL)
                .delete(body)
                .build();

        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.code() == HttpServletResponse.SC_OK) {
                        instructionsTable.getItems().forEach(entry -> { entry.setBreakpoint(false); });
                        Platform.runLater(() -> {
                            instructionsTable.refresh();
                        });
                    }
                    else if (response.code() == HttpServletResponse.SC_NO_CONTENT) {
                        showAlert("There is no program to delete breakpoints from.", (Stage) clearBreakpointsBtn.getScene().getWindow());
                    }
                    else {
                        showAlert("Triggered a success response but is not conventional", (Stage) clearBreakpointsBtn.getScene().getWindow());
                    }
                }
                else {
                    showAlert("Failed to delete all breakpoints" + "\n" + "Code: " + response.code(), (Stage) clearBreakpointsBtn.getScene().getWindow());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showAlert("Failed to send a request to delete all breakpoints ", (Stage) clearBreakpointsBtn.getScene().getWindow());
            }
        });
    }

    public boolean isArchitectureEnough(String currentArchitecture, String entryArchitecture) {
        try {
            return (ArchitectureGeneration.valueOf(currentArchitecture).compareTo(ArchitectureGeneration.valueOf(entryArchitecture)) >= 0);
        } catch (IllegalArgumentException e) {
            return true; // Assuming the only case architecture is not an enum is before user choice
        }
    }

    public void refreshInstructionTable() {
        Platform.runLater(() -> instructionsTable.refresh());
    }

    public MenuButton getExpansionLevelMenu() {
        return expansionLevelMenu;
    }

    public void initAllFields() {
        if (primaryController.program != null) {
            updateMainInstructionTable();
            Platform.runLater(() -> {
                maxLevel.set(primaryController.program.getMaxExpandLevel());
                updateVariablesOrLabelSelectionMenu();
            });
            setCurrentLevel(0);
        }
    }
}