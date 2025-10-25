package execution.controller;

import com.google.gson.Gson;
import configuration.HTTPCodes;
import dto.ArchitectureGeneration;
import dto.ExecutionPayload;
import dto.ProgramData;
import execution.refreshTasks.PullProgramInfoTask;
import jakarta.servlet.http.HttpServletResponse;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import execution.model.ArgumentTableEntry;
import execution.model.InstructionTableEntry;
import dto.VariableDTO;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

import static configuration.ClientConfiguration.CLIENT;
import static configuration.DialogUtils.showAlert;
import static configuration.ResourcesConfiguration.*;

public class RightSideController{

    private PrimaryController primaryController;
    private TopComponentController topController;
    private LeftSideController leftController;
    private Stage primaryStage;
    private String currentlyChosenArchitecture;
    private final BooleanProperty isDebugMode = new SimpleBooleanProperty(false);
    private final IntegerProperty currentCycles = new SimpleIntegerProperty(-1);
    private final SimpleIntegerProperty nextInstructionIdForDebug = new SimpleIntegerProperty(0);

    @FXML
    private TableView<ArgumentTableEntry> executionArgumentInput;

    @FXML
    private Button ResumeDebugBtn;

    @FXML
    private Button RunProgramBtn;

    @FXML
    private TableColumn<ArgumentTableEntry, String> argumentNamesColumn;

    @FXML
    private TableColumn<ArgumentTableEntry, Number> argumentValuesColumn;

    @FXML
    private Button StepOverDebugBtn;

    @FXML
    private Button SetUpRunBtn;

    @FXML
    private RadioButton debugRadioButton;

    @FXML
    private RadioButton runRadioButton;

    @FXML
    private Button StopDebugBtn;

    @FXML
    private TableView<ArgumentTableEntry> variableTable;

    @FXML
    private TableColumn<ArgumentTableEntry, String> resultVariableNameColumn;

    @FXML
    private TableColumn<ArgumentTableEntry,Number> resultVariableValueColumn;

    @FXML
    private Label cyclesLabel;

    @FXML
    private Button backToDashboardBtn;

    @FXML
    private MenuButton architectureMenu;

    @FXML
    public void initialize() {
        /* Tables */
        argumentNamesColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        argumentValuesColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        resultVariableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        resultVariableValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        argumentValuesColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        argumentValuesColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));

        // Update underlying model when editing finishes
        argumentValuesColumn.setOnEditCommit(event -> {
            ArgumentTableEntry entry = event.getRowValue();
            entry.valueProperty().set(event.getNewValue().intValue());
        });

        variableTable.getSortOrder().clear();
        variableTable.setSortPolicy(param -> null); // disables sorting globally
        executionArgumentInput.getSortOrder().clear();
        executionArgumentInput.setSortPolicy(param -> null); // disables sorting globally

        // Allow editing only on values
        executionArgumentInput.setEditable(true);
        executionArgumentInput.getSelectionModel().setCellSelectionEnabled(true);

        variableTable.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, Event::consume); // Disable selection from user only

        // Minimize tables' height to prevent vertical scrolling
        variableTable.setPrefHeight(variableTable.getPrefHeight() * 0.85);
        executionArgumentInput.setPrefHeight(executionArgumentInput.getPrefHeight() * 0.85);

        /* Buttons initialization */
        // Radio buttons
        ToggleGroup modeToggleGroup = new ToggleGroup();
        debugRadioButton.setToggleGroup(modeToggleGroup);
        runRadioButton.setToggleGroup(modeToggleGroup);

        // Menu buttons
        updateAvailableArchitecture();
        architectureMenu.disableProperty().bind(isDebugMode);

        // Debugging buttons
        StepOverDebugBtn.disableProperty().bind(isDebugMode.not());
        ResumeDebugBtn.disableProperty().bind(isDebugMode.not());
        StopDebugBtn.disableProperty().bind(isDebugMode.not());

        // Regular buttons
        SetUpRunBtn.disableProperty().bind(
                isDebugMode
        );

        /* Labels */
        // Initialize the cycles label
        cyclesLabel.textProperty().bind(Bindings.createStringBinding(
                () -> (currentCycles.get() < 0) ? "Cycles: ---" : "Cycles: " + currentCycles.get(),
                currentCycles
        ));

    }

    @FXML
    public void ResumeDebugPressed(ActionEvent event) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESUME_DEBUG_RESOURCE))
                .newBuilder();
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
                    try (ResponseBody body = response.body()) {
                        Gson gson = new Gson();
                        primaryController.program = gson.fromJson(Objects.requireNonNull(body).string(), ProgramData.class);
                        updateAfterDebugStep();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (response.code() == HttpServletResponse.SC_PAYMENT_REQUIRED){
                        showAlert("User credits too low for Program execution - Credits were not sufficient to execute the entire program"
                                , (Stage) runRadioButton.getScene().getWindow());
                    } else {
                    showAlert("Resume debug failed with code: " + response.code(),
                            (Stage) ResumeDebugBtn.getScene().getWindow());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Open a timer task for pulling information about execution
                PullProgramInfoTask pullProgramInfoTask = new PullProgramInfoTask(RunProgramBtn, primaryController);
                Timer timer = new Timer(true);
                timer.schedule(pullProgramInfoTask, 2000, 2000);
            }
        });
    }

    @FXML
    public void RunProgramPressed(ActionEvent event) {

        if (runRadioButton.isSelected()) {
            Set<VariableDTO> argumentValues = executionArgumentInput.getItems().stream()
                    .map(entry-> new VariableDTO(entry.getName(), entry.getValue())) // ArgumentTableEntry -> VariableDTO
                    .collect(Collectors.toSet());

            // Pass them to runProgram
            try {
                sendRunProgramRequest(argumentValues);
            } catch (Exception e) {
                showAlert(e.getMessage(), (Stage)runRadioButton.getScene().getWindow());
            }
        }
        else {
            try {
                StartDebugPressed();
            } catch (Exception e) {
                showAlert(e.getMessage(), (Stage)debugRadioButton.getScene().getWindow());
            }
        }
    }

    @FXML
    void StepOverDebugPressed(ActionEvent event) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + STEP_OVER_RESOURCE))
                .newBuilder();
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
                    try (ResponseBody body = response.body()) {
                        Gson gson = new Gson();
                        primaryController.program = gson.fromJson(Objects.requireNonNull(body).string(), ProgramData.class);
                        updateAfterDebugStep();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    topController.sendUpdateCreditsRequest();
                } else {
                    if(response.code() == HttpServletResponse.SC_PAYMENT_REQUIRED){
                        showAlert("User credits too low for Program execution - Credits were not sufficient to execute the entire program"
                                , (Stage) runRadioButton.getScene().getWindow());
                    }else {
                        showAlert("Step over failed with code: " + response.code(),
                                (Stage) StepOverDebugBtn.getScene().getWindow());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Open a timer task for pulling information about execution
                PullProgramInfoTask pullProgramInfoTask = new PullProgramInfoTask(RunProgramBtn, primaryController);
                Timer timer = new Timer(true);
                timer.schedule(pullProgramInfoTask, 2000, 2000);
            }
        });
    }

    @FXML
    void StopDebugPressed(ActionEvent event) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + STOP_DEBUG_RESOURCE))
                .newBuilder();
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
                    try (ResponseBody body = response.body()) {
                        Gson gson = new Gson();
                        primaryController.program = gson.fromJson(Objects.requireNonNull(body).string(), ProgramData.class);
                        updateIsDebugProperty();
                        leftController.clearMarkInInstructionTable();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    showAlert("Stop debug failed with code: " + response.code(),
                            (Stage) StepOverDebugBtn.getScene().getWindow());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void SetupNewRunPressed(ActionEvent event) {
        Platform.runLater(() -> {
            variableTable.getItems().clear();
            executionArgumentInput.getItems().forEach(entry->{ entry.valueProperty().set(0); });
            currentCycles.set(0);
        });
    }

    @FXML
    void returnToDashboardScreen(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/resources/fxml/dashboard.fxml"));
                Parent newRoot = loader.load();
                Scene dashboardScene = new Scene(newRoot, 850, 600);

                // Copy current scene stylesheets (preserves chosen skin)
                Scene currentScene = backToDashboardBtn.getScene();

                dashboardScene.getStylesheets().setAll(currentScene.getStylesheets());

                // Fallback: ensure at least a default stylesheet if none copied
                if (dashboardScene.getStylesheets().isEmpty()) { // Case: no stylesheet was copied because empty
                    dashboardScene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
                }

                dashboard.controller.PrimaryController controller = loader.getController();

                // Close current window
                ((Stage) currentScene.getWindow()).close();

                controller.getTopComponentController().setPrimaryStage(primaryStage);
                primaryStage.setScene(dashboardScene);
                primaryStage.setTitle("S-embler - Dashboard");
                primaryStage.getIcons().add(
                        new Image(getClass().getResourceAsStream("/resources/icon.png"))
                );
                primaryStage.show();
            } catch (Exception ex) {
                Stage primaryStage = (Stage) backToDashboardBtn.getScene().getWindow();
                showAlert("Failed to load dashboard: " + ex.getMessage(), primaryStage);
            }
        });
    }

    public void setPrimaryController(PrimaryController primaryController) {
        this.primaryController = primaryController;

        variableTable.placeholderProperty().bind(
                Bindings.when(Bindings.createBooleanBinding(
                                () -> primaryController.program == null
                        ))
                        .then(new Label("No program loaded."))
                        .otherwise(new Label("No variables state to present"))
        );
    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setLeftController(LeftSideController leftController) {
        this.leftController = leftController;

        RunProgramBtn.disableProperty().bind(
                isDebugMode.or(
                        Bindings.createBooleanBinding(() -> {
                                    if (primaryController == null || primaryController.program == null) {
                                        return true; // Disable if dependencies are not injected
                                    }
                                    // Disable if architecture is not enough
                                    return !leftController.isArchitectureEnough(
                                            architectureMenu.getText(),
                                            primaryController.program.getMinimalArchitectureNeededForRun()
                                    );
                                },
                                architectureMenu.textProperty(),
                                leftController.getExpansionLevelMenu().textProperty())
                )
        );
    }

    public void updateArgumentTable() {
        List<ArgumentTableEntry> entries = primaryController.program.getProgramXArguments().stream()
                .map(ArgumentTableEntry::new) // Convert ArgumentDTO -> ArgumentTableEntry
                .toList();
        Platform.runLater(() -> executionArgumentInput.getItems().setAll(entries)); // Replace items in the table
    }

    public void updateResultVariableTable() {
        Platform.runLater(() -> {
            List<ArgumentTableEntry> previousEntries = new ArrayList<>(variableTable.getItems());
            List<ArgumentTableEntry> newEntries = primaryController.program.getProgramVariablesCurrentState().stream()
                    .map(ArgumentTableEntry::new) // Convert VariableDTO -> ArgumentTableEntry
                    .toList();

            variableTable.getItems().setAll(newEntries); // Replace items in the table

            for (int i = 0; i < newEntries.size(); i++) {
                ArgumentTableEntry newEntry = newEntries.get(i);
                if (i < previousEntries.size()) {
                    ArgumentTableEntry oldEntry = previousEntries.get(i);
                    // Compare by value
                    if (newEntry.getValue() != oldEntry.getValue()) {
                        variableTable.getSelectionModel().select(i);
                    }
                    else {
                        variableTable.getSelectionModel().clearSelection(i);
                    }
                }
            }


        });
    }

    void StartDebugPressed() {

        if (currentlyChosenArchitecture == null) {
            showAlert("Please choose the architecture to start debug.", primaryStage);
            return;
        }

        Set<VariableDTO> argumentValues = executionArgumentInput.getItems().stream()
                .map(entry-> new VariableDTO(entry.getName(), entry.getValue())) // ArgumentTableEntry -> VariableDTO
                .collect(Collectors.toSet());
        Set<Integer> breakpoints = leftController.getEntriesWithBreakpoints().stream()
                .map(InstructionTableEntry::getId).collect(Collectors.toSet());

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + START_DEBUG_RESOURCE))
                .newBuilder();
        String finalURL = urlBuilder.build().toString();

        // Create a temporal object containing arguments and breakpoints to charge as json
        Gson gson = new Gson();
        String json = gson.toJson(Map.of("arguments", argumentValues,
                "breakpoints", breakpoints,
                "architectureGeneration", currentlyChosenArchitecture));

        // Build the body sent to the server to include the debug required object
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
                if (response.isSuccessful()) {
                    try (ResponseBody body = response.body()) {
                        Gson gson = new Gson();
                        primaryController.program = gson.fromJson(Objects.requireNonNull(body).string(), ProgramData.class);
                        nextInstructionIdForDebug.set(primaryController.program.getNextInstructionIdForDebug());
                        leftController.markEntryInInstructionTable(nextInstructionIdForDebug.get()-1);
                        updateResultVariableTable();
                        updateIsDebugProperty();
                        leftController.clearHistoryChainTable(); // Clear history chain table on new debug start
                        updateAfterDebugStep();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (response.code() == HTTPCodes.UNPROCESSABLE_ENTITY) {
                        showAlert("Architecture Generation ("
                                        +currentlyChosenArchitecture
                                        + ") too low for Program execution ("
                                        +primaryController.program.getMinimalArchitectureNeededForRun()+")"
                                , (Stage) runRadioButton.getScene().getWindow());
                    } else if (response.code() == HttpServletResponse.SC_NOT_ACCEPTABLE) {
                        showAlert("User credits too low for Program execution"
                                , (Stage) runRadioButton.getScene().getWindow());
                    }
                    else if (response.code() == HttpServletResponse.SC_PAYMENT_REQUIRED){
                        Stage executionStage = (Stage) runRadioButton.getScene().getWindow();
                        showAlert("User credits too low for Program execution - Credits were not sufficient to execute the entire program"
                                , (Stage) runRadioButton.getScene().getWindow());


                    }
                    else {
                        showAlert("Failed to execute program in architecture " + currentlyChosenArchitecture + "\n" + "Code: " + response.code(),
                                (Stage) runRadioButton.getScene().getWindow());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    void updateAfterDebugStep() {
        nextInstructionIdForDebug.set(primaryController.program.getNextInstructionIdForDebug());
        if (!primaryController.program.isDebugmode()){ // Debugging finished
            nextInstructionIdForDebug.set(0);
            leftController.clearMarkInInstructionTable();
        }
        leftController.markEntryInInstructionTable(nextInstructionIdForDebug.get()-1);
        updateResultVariableTable();
        updateIsDebugProperty();
        updateCycles();
        topController.sendUpdateCreditsRequest();
    }

    public void updateIsDebugProperty() {
        if (primaryController.program != null) {
            isDebugMode.set(primaryController.program.isDebugmode());
        } else {
            isDebugMode.set(false);
        }
    }

    public void updateAvailableArchitecture() {
        architectureMenu.getItems().clear();

        Arrays.stream(ArchitectureGeneration.values())
                .map(architecture -> {
                    String architectureName = architecture.toString();

                    Label label = new Label(architectureName);
                    label.setMaxWidth(Double.MAX_VALUE);
                    label.setStyle("-fx-alignment: center;");

                    CustomMenuItem menuItem = new CustomMenuItem(label, true);
                    menuItem.setUserData(architectureName);

                    label.prefWidthProperty().bind(architectureMenu.widthProperty());

                    menuItem.setOnAction((ActionEvent event) -> {
                        String chosenArchitecture = (String) menuItem.getUserData();
                        currentlyChosenArchitecture = chosenArchitecture;
                        Platform.runLater(() -> architectureMenu.setText(chosenArchitecture));
                        leftController.refreshInstructionTable();
                    });
                    return menuItem;
                })

                .forEach(architectureMenu.getItems()::add);
    }

    public void sendRunProgramRequest(Set<VariableDTO> arguments) {

        if (currentlyChosenArchitecture == null) {
            showAlert("You must choose an architecture before running the program.", primaryStage);
            return;
        }

        ExecutionPayload executionPayload = new ExecutionPayload(arguments,currentlyChosenArchitecture);

        Gson gson = new Gson();
        String jsonPayload = gson.toJson(executionPayload);

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RUN_PROGRAM_RESOURCE))
                .newBuilder();
        String finalURL = urlBuilder.build().toString();

        RequestBody body = RequestBody.create(
                jsonPayload,
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
                if (response.isSuccessful()) {
                    if (response.code() == HttpServletResponse.SC_OK) {
                        try (ResponseBody responseBody = response.body()) {
                            Gson gson = new Gson();
                            primaryController.program = gson.fromJson(Objects.requireNonNull(responseBody).string(), ProgramData.class);
                            leftController.updateMainInstructionTable();
                            updateResultVariableTable();
                            updateCycles();
                        } catch (InvalidParameterException e) {
                            showAlert(e.getMessage(), (Stage) runRadioButton.getScene().getWindow());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == HttpServletResponse.SC_NO_CONTENT) {
                        showAlert("No program data detected for the user.", (Stage) runRadioButton.getScene().getWindow());
                    }
                    topController.sendUpdateCreditsRequest();
                } else {
                    if (response.code() == HTTPCodes.UNPROCESSABLE_ENTITY) {
                        showAlert("Architecture Generation ("
                                +currentlyChosenArchitecture
                                + ") too low for Program execution ("
                                +primaryController.program.getMinimalArchitectureNeededForRun()+")"
                                , (Stage) runRadioButton.getScene().getWindow());
                    } else if (response.code() == HttpServletResponse.SC_NOT_ACCEPTABLE) {
                        showAlert("User credits too low for Program execution - Lower than average cost for program"
                                , (Stage) runRadioButton.getScene().getWindow());
                    } else if(response.code() == HttpServletResponse.SC_PAYMENT_REQUIRED){
                        showAlert("User credits too low for Program execution - Credits were not sufficient to execute the entire program"
                                , (Stage) runRadioButton.getScene().getWindow());
                    }
                    else {
                        showAlert("Failed to execute program in architecture " + currentlyChosenArchitecture + "\n" + "Code: " + response.code(),
                                (Stage) runRadioButton.getScene().getWindow());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Open a timer task for pulling information about execution
                PullProgramInfoTask pullProgramInfoTask = new PullProgramInfoTask(RunProgramBtn, primaryController);
                Timer timer = new Timer(true);
                timer.schedule(pullProgramInfoTask, 2000, 2000);
            }
        });
    }

    private void updateBindings() {
        // get() invoke triggers bind re-evaluation including re-evaluation of primaryController.program current value
        RunProgramBtn.disableProperty().get();
        SetUpRunBtn.disableProperty().get();
        variableTable.placeholderProperty().get();
    }

    public BooleanProperty isInDebugModeProperty() {
        return isDebugMode;
    }

    public void updateCycles() { Platform.runLater(() -> currentCycles.set(primaryController.program.getCurrentCycles())); }

    public String getSelectedArchitecture() {
        return architectureMenu.getText();
    }

    public TableView<ArgumentTableEntry> getExecutionArgumentInput() {
        return executionArgumentInput;
    }

    public MenuButton getArchitectureMenu() {
        return architectureMenu;
    }

    public void initAllFields() {
        if (primaryController.program != null) {
            updateArgumentTable();
            updateResultVariableTable();
            variableTable.getItems().forEach(item -> item.setValue(0));
            Platform.runLater(() -> {
                currentCycles.set(0);
                updateBindings();
            });
        }
    }

    public void clearVariablesTable() {
        variableTable.getItems().forEach(item -> item.setValue(0));
    }
}