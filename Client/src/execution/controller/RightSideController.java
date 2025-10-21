package execution.controller;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import execution.model.ArgumentTableEntry;
import execution.model.InstructionTableEntry;
import dto.VariableDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static configuration.DialogUtils.showAlert;

public class RightSideController{

    private PrimaryController primaryController;
    private TopComponentController topController;
    private LeftSideController leftController;
    private Stage primaryStage;
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

        /* Buttons initialization */
        // Radio buttons
        ToggleGroup modeToggleGroup = new ToggleGroup();
        debugRadioButton.setToggleGroup(modeToggleGroup);
        runRadioButton.setToggleGroup(modeToggleGroup);

        // Debugging buttons
        StepOverDebugBtn.disableProperty().bind(isDebugMode.not());
        ResumeDebugBtn.disableProperty().bind(isDebugMode.not());
        StopDebugBtn.disableProperty().bind(isDebugMode.not());

        // Initialize the cycles label
        cyclesLabel.textProperty().bind(Bindings.createStringBinding(
                () -> (currentCycles.get() < 0) ? "Cycles: ---" : "Cycles: " + currentCycles.get(),
                currentCycles
        ));

        variableTable.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, Event::consume); // Disable selection from user only

        // Minimize tables' height to prevent vertical scrolling
        variableTable.setPrefHeight(variableTable.getPrefHeight() * 0.85);
        executionArgumentInput.setPrefHeight(executionArgumentInput.getPrefHeight() * 0.85);
    }

    @FXML
    public void ResumeDebugPressed(ActionEvent event) {
        //model.resumeDebug();
//        updateAfterDebugStep();
    }

    @FXML
    public void RunProgramPressed(ActionEvent event) {

        if (runRadioButton.isSelected()) {
            Set<VariableDTO> argumentValues = executionArgumentInput.getItems().stream()
                    .map(entry-> new VariableDTO(entry.getName(), entry.getValue())) // ArgumentTableEntry -> VariableDTO
                    .collect(Collectors.toSet());

            // Pass them to runProgram
            try {
                //model.runProgram(argumentValues);
            } catch (Exception e) {
                Alert alert = createErrorMessageOnRunProgram(e);
                alert.showAndWait();
            }
//            updateResultVariableTable();
//            updateCycles();
        }
        else {
            try {
                StartDebugPressed(event);
            } catch (Exception e) {
                Alert alert = createErrorMessageOnRunProgram(e);
                alert.showAndWait();
            }
        }
    }

    @FXML
    void StepOverDebugPressed(ActionEvent event) {
        //model.stepOver();
//        updateAfterDebugStep();
    }

    @FXML
    void StopDebugPressed(ActionEvent event) {
        //model.stopDebug();
//        updateIsDebugProperty();
        leftController.clearMarkInInstructionTable();
    }

    @FXML
    void SetupNewRunPressed(ActionEvent event) {
        variableTable.getItems().clear();
        executionArgumentInput.getItems().forEach(entry->{entry.valueProperty().set(0);});
        currentCycles.set(0);
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

        SetUpRunBtn.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> primaryController.program == null || isDebugMode.get(),
                        new SimpleObjectProperty<>(primaryController.program),
                        isDebugMode
                )
        );

        RunProgramBtn.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> primaryController.program == null || isDebugMode.get(),
                        new SimpleObjectProperty<>(primaryController.program),
                        isDebugMode
                )
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
    }

    private Alert createErrorMessageOnRunProgram(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK); // Helps access and style the ok button
        okButton.setId("ok-button");
        alert.initOwner(primaryStage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Error");
        alert.setHeaderText("Program Run Failed");
        alert.setContentText(e.getMessage());

        // Ensure the dialog is focused
        alert.setOnShown(dialogEvent -> alert.getDialogPane().requestFocus());

        return alert;
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

    void StartDebugPressed(ActionEvent event) {
        Set<VariableDTO> argumentValues = executionArgumentInput.getItems().stream()
                .map(entry-> new VariableDTO(entry.getName(), entry.getValue())) // ArgumentTableEntry -> VariableDTO
                .collect(Collectors.toSet());
        Set<Integer> breakpoints = leftController.getEntriesWithBreakpoints().stream()
                .map(InstructionTableEntry::getId).collect(Collectors.toSet());
        //model.startDebug(argumentValues, breakpoints);
        //model.getProgramData().ifPresent(model->nextInstructionIdForDebug.set(model.getNextInstructionIdForDebug()));
        leftController.markEntryInInstructionTable(nextInstructionIdForDebug.get()-1);
//        updateResultVariableTable();
//        updateIsDebugProperty();
        leftController.clearHistoryChainTable(); // Clear history chain table on new debug start
//        updateAfterDebugStep();
    }

//    void updateAfterDebugStep() {
//        model.getProgramData().ifPresent(model-> {
//            nextInstructionIdForDebug.set(model.getNextInstructionIdForDebug());
//            if (!model.isDebugmode()){ // Debugging finished
//                nextInstructionIdForDebug.set(0);
//                leftController.clearMarkInInstructionTable();
//
//            }
//            leftController.markEntryInInstructionTable(nextInstructionIdForDebug.get()-1);
//        });
//        updateResultVariableTable();
//        updateIsDebugProperty();
//        updateCycles();
//    }

//    public void updateIsDebugProperty() {
//        if (model.isProgramLoaded()) {
//            model.getProgramData().ifPresent(data ->
//                    isDebugMode.set(data.isDebugmode())
//            );
//        } else {
//            isDebugMode.set(false);
//        }
//    }

    public BooleanProperty isInDebugModeProperty() {
        return isDebugMode;
    }

    public void clearVariableTable() {
        variableTable.getItems().clear();
    }

    public void initAllFields() {
        if (primaryController.program != null) {
            updateArgumentTable();
            updateResultVariableTable();
            Platform.runLater(() -> currentCycles.set(primaryController.program.getCurrentCycles()));
        }
    }
}