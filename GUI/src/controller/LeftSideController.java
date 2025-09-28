package controller;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import model.InstructionTableEntry;
import program.data.InstructionDTO;
import program.data.Searchable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeftSideController {

    private RightSideController rightController;
    private TopComponentController topController;
    private SingleProgramController model;
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(-1);
    private IntegerProperty maxLevel = new SimpleIntegerProperty(-1);
    private final int HISTORY_CHAIN_EFFECT_DURATION = 300; // milliseconds

    @FXML
    private TableView<InstructionTableEntry> chosenInstructionHistoryTable;

    @FXML
    private MenuButton functionChooser;

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

    public void setModel(SingleProgramController model) {
        this.model = model;
    }

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;

        // Initialize the max label
        maxExpandLevel.textProperty().bind(
                Bindings.when(rightController.isProgramLoaded())
                        .then(Bindings.createStringBinding(() -> "/" + maxLevel.get(), maxLevel
                        ))
                        .otherwise("/Max")
        );

        functionChooser.disableProperty().bind(
                Bindings.isEmpty(highlightSelection.getItems())
                        .or(rightController.isInDebugModeProperty())
        );

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
//            if (rightController.isInDebugModeProperty().get()) {
//                event.consume();
//            }

            if (rightController.isInDebugModeProperty().get()) {
                // Allow interaction only if the event target is a TableCell in the id column
                Node target = event.getTarget() instanceof Node ? (Node) event.getTarget() : null;
                while (target != null && !(target instanceof TableCell)) {
                    target = target.getParent();
                }
                if (target instanceof TableCell<?, ?> cell) {
                    TableColumn<?, ?> column = cell.getTableColumn();
                    if ("idColumn".equals(column.getId())) {
                        return; // allow event for id column
                    }
                }
                event.consume(); // block all other interactions
            }
        });

        summaryLine.textProperty().bind(
                Bindings.when(rightController.isProgramLoaded())
                        .then(Bindings.createStringBinding(() ->
                                        "Program contains " +
                                                instructionsTable.getItems().stream().filter(entry -> "B".equals(entry.getType())).count() +
                                                " Basic Instructions, and " +
                                                instructionsTable.getItems().stream().filter(entry -> "S".equals(entry.getType())).count() +
                                                " Synthetic Instructions",
                                instructionsTable.getItems()
                        ))
                        .otherwise("No program loaded.")
        );

        // Add this in setRightController or appropriate initialization method
        chosenInstructionHistoryTable.placeholderProperty().bind(
                Bindings.createObjectBinding(() -> {
                    if (!rightController.isProgramLoadedProperty().get()) {
                        return new Label("No program loaded.");
                    } else if (instructionsTable.getSelectionModel().getSelectedItem() == null) {
                        return new Label("Choose an instruction from the table above to present its history.");
                    } else {
                        return new Label("This instruction has no history");
                    }
                }, rightController.isProgramLoadedProperty(), instructionsTable.getSelectionModel().selectedItemProperty())
        );

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
                                    model.addBreakpoint(this.getItem().intValue()); // Remove any existing breakpoint first to avoid duplicates
                                    setText(null);
                                    setGraphic(wrapper);
                                    circle.setFill(Color.rgb(217, 83, 79, 1.0));
                                    if (entry != null) entry.setBreakpoint(true);
                                } else {
                                    model.removeBreakpoint(this.getItem().intValue());
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
                                model.addBreakpoint(entry.getId());
                                isActive = true;
                                setText(null);
                                setGraphic(wrapper);
                                circle.setFill(Color.rgb(217, 83, 79, 1.0));
                            } else {
                                if (entry != null) {
                                    model.removeBreakpoint(entry.getId());
                                }
                                isActive = false;
                                setText(item.toString());
                                setGraphic(null);
                            }
                        }
                    });
                });
    }

    public void updateMainInstructionTable() {
        model.getProgramData().ifPresent(programData -> {
            List<InstructionTableEntry> entries = programData.getProgramInstructions().stream()
                    .map(InstructionTableEntry::new) // Convert InstructionDTO -> InstructionTableEntry
                    .toList();
            instructionsTable.getItems().setAll(entries);// Replace items in the table
            clearAllBreakpoints(null); // Clear all breakpoints when loading a new function, a new program or when changing the expansion level
        });
    }

    public void updateMaxExpansionLevel() {
        model.getProgramData().ifPresent(programData -> {
            maxLevel.set(programData.getMaxExpandLevel());
        });
    }

    public void setCurrentLevel(int level) {
        currentLevel.set(level);
        expansionLevelMenu.setText(String.valueOf(level));
    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    @FXML
    public void initialize() {

        instructionsTable.setRowFactory(tv -> {
            TableRow<InstructionTableEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    updateParentInstructionTable(row.getItem().getInstructionDTO());
                }
            });
            return row;
        });

        // Initialize the menu button of the expansion levels
        expansionLevelMenu.getItems().clear();
        maxLevel.addListener((obs, oldValue, newValue) -> {
            updateAvailableExpansionLevels(newValue.intValue());
        });

        // Initialize the highlight selection menu button
        highlightSelection.getItems().clear();

        // Initialize the program or function selectin menu button
        functionChooser.getItems().clear();

        // Initialize the history chain table and disable row selection
        chosenInstructionHistoryTable.setSelectionModel(null);

        instructionsTable.getSortOrder().clear();
        instructionsTable.setSortPolicy(param -> null); // Disables sorting globally
        chosenInstructionHistoryTable.getSortOrder().clear();
        chosenInstructionHistoryTable.setSortPolicy(param -> null); // Disables sorting globally

        // Minimize tables' height to prevent vertical scrolling
        instructionsTable.setPrefHeight(instructionsTable.getPrefHeight() * 0.85);
        chosenInstructionHistoryTable.setPrefHeight(chosenInstructionHistoryTable.getPrefHeight() * 0.85);
    }

    public void markEntryInInstructionTable(int entryId) {
        instructionsTable.getSelectionModel().clearAndSelect(entryId);
        instructionsTable.getFocusModel().focus(entryId);
        instructionsTable.scrollTo(entryId);
    }

    public void clearMarkInInstructionTable() {
        instructionsTable.getSelectionModel().clearSelection();
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
                        setCurrentLevel(selectedLevel);
                        model.Expand(currentLevel.get());
                        updateMainInstructionTable();
                        updateVariablesOrLabelSelectionMenu();
                    });
                    return menuItem;
                })
                .forEach(expansionLevelMenu.getItems()::add);
    }

    public void updateFunctionOrProgramSelectionMenu() {
        model.getProgramData().ifPresent(programData -> {
            List<String> functionNames = programData.getAllFunctionNames();
            // Clear existing items
            functionChooser.getItems().clear();

            // Sort function names alphabetically, keeping the first (main program) at the top
            List<String> sortedFunctionNames = new ArrayList<>(functionNames);
            if (sortedFunctionNames.size() > 1) {
                List<String> toSort = sortedFunctionNames.subList(1, sortedFunctionNames.size());
                toSort.sort(String::compareTo);
            }

            // Create item entry for each function name
            sortedFunctionNames.forEach(functionName -> {
                Label label = new Label(functionName);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setStyle("-fx-alignment: center;");
                CustomMenuItem choice = new CustomMenuItem(label, true);
                choice.setUserData(functionName);
                label.prefWidthProperty().bind(functionChooser.widthProperty());
                choice.setOnAction((ActionEvent event) -> {
                    model.switchFunction(functionName);
                    functionChooser.setText(functionName);
                    updateMainInstructionTable();
                    updateMaxExpansionLevel();
                    setCurrentLevel(0);
                    updateAvailableExpansionLevels(maxLevel.get());
                    updateVariablesOrLabelSelectionMenu();
                    clearHistoryChainTable();
                    clearMarkInInstructionTable();
                    rightController.updateCycles();
                    rightController.updateArgumentTable();
                    rightController.updateResultVariableTable();
                    rightController.updateStatisticsTable();
                });
                functionChooser.getItems().add(choice);
            });
        });
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

    public void clearHistoryChainTable() {
        chosenInstructionHistoryTable.getItems().clear();
    }

    public void setProgramNameInChooser() {
        model.getProgramData().ifPresent(programData -> {
            functionChooser.setText(programData.getAllFunctionNames().getFirst());
        });
    }

    public Set<InstructionTableEntry> getEntriesWithBreakpoints() {
        return instructionsTable.getItems().stream()
                .filter(InstructionTableEntry::isBreakpoint)
                .collect(Collectors.toSet());
    }

    @FXML
    public void clearAllBreakpoints(ActionEvent event) {
        instructionsTable.getItems().forEach(entry -> { entry.setBreakpoint(false); });
        instructionsTable.refresh();
    }
}