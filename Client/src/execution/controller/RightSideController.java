package execution.controller;

import controller.SingleProgramController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import execution.model.ArgumentTableEntry;
import execution.model.InstructionTableEntry;
import classes.VariableDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RightSideController{

    private TopComponentController topController;
    private LeftSideController leftController;
    private Stage primaryStage;
    private SingleProgramController model;
    private final BooleanProperty isDebugMode = new SimpleBooleanProperty(false);
    private final BooleanProperty isProgramLoaded = new SimpleBooleanProperty(false);
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

        SetUpRunBtn.disableProperty().bind(
                isProgramLoaded.not().or(isDebugMode)
        );

        RunProgramBtn.disableProperty().bind(
                isProgramLoaded.not().or(isDebugMode)
        );

        // Initialize the cycles label
        cyclesLabel.textProperty().bind(Bindings.createStringBinding(
                () -> (currentCycles.get() < 0) ? "Cycles: ---" : "Cycles: " + currentCycles.get(),
                currentCycles
        ));

        variableTable.placeholderProperty().bind(
                Bindings.when(isProgramLoaded.not())
                        .then(new Label("No program loaded."))
                        .otherwise(new Label("No variables state to present"))
        );
        variableTable.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, Event::consume); // Disable selection from user only

        // Minimize tables' height to prevent vertical scrolling
        variableTable.setPrefHeight(variableTable.getPrefHeight() * 0.85);
        executionArgumentInput.setPrefHeight(executionArgumentInput.getPrefHeight() * 0.85);
    }

    @FXML
    public void ResumeDebugPressed(ActionEvent event) {
        model.resumeDebug();
        updateAfterDebugStep();
    }

    @FXML
    public void RunProgramPressed(ActionEvent event) {

        if (runRadioButton.isSelected()) {
            Set<VariableDTO> argumentValues = executionArgumentInput.getItems().stream()
                    .map(entry-> new VariableDTO(entry.getName(), entry.getValue())) // ArgumentTableEntry -> VariableDTO
                    .collect(Collectors.toSet());

            // Pass them to runProgram
            try {
                model.runProgram(argumentValues);
            } catch (Exception e) {
                Alert alert = createErrorMessageOnRunProgram(e);
                alert.showAndWait();
            }
            updateResultVariableTable();
            updateCycles();
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
    void StartDebugPressed(ActionEvent event) {
        Set<VariableDTO> argumentValues = executionArgumentInput.getItems().stream()
                .map(entry-> new VariableDTO(entry.getName(), entry.getValue())) // ArgumentTableEntry -> VariableDTO
                .collect(Collectors.toSet());
        Set<Integer> breakpoints = leftController.getEntriesWithBreakpoints().stream()
                .map(InstructionTableEntry::getId).collect(Collectors.toSet());
        model.startDebug(argumentValues, breakpoints);
        model.getProgramData().ifPresent(model->nextInstructionIdForDebug.set(model.getNextInstructionIdForDebug()));
        leftController.markEntryInInstructionTable(nextInstructionIdForDebug.get()-1);
        updateResultVariableTable();
        updateIsDebugProperty();
        leftController.clearHistoryChainTable(); // Clear history chain table on new debug start
        updateAfterDebugStep();
    }

    @FXML
    void StepOverDebugPressed(ActionEvent event) {
        model.stepOver();
        updateAfterDebugStep();
    }

    @FXML
    void StopDebugPressed(ActionEvent event) {
        model.stopDebug();
        updateIsDebugProperty();
        leftController.clearMarkInInstructionTable();
    }

    @FXML
    void SetupNewRunPressed(ActionEvent event) {
        variableTable.getItems().clear();
        executionArgumentInput.getItems().forEach(entry->{entry.valueProperty().set(0);});
        currentCycles.set(0);
    }

    public void setModel(SingleProgramController model) {

        this.model = model;
        isProgramLoaded.set(model.isProgramLoaded());
    }

    public void OnProgramLoaded() {
        updateCycles();
        isProgramLoaded.set(true);
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
        model.getProgramData().ifPresent(programData -> {
            List<ArgumentTableEntry> entries = programData.getProgramXArguments().stream()
                    .map(ArgumentTableEntry::new) // Convert ArgumentDTO -> ArgumentTableEntry
                    .toList();
            executionArgumentInput.getItems().setAll(entries);// Replace items in the table
        })
        ;
    }

    public void updateResultVariableTable() {
        model.getProgramData().ifPresent(programData -> {
            List<ArgumentTableEntry> previousEntries = new ArrayList<>(variableTable.getItems());
            List<ArgumentTableEntry> entries = programData.getProgramVariablesCurrentState().stream()
                    .map(ArgumentTableEntry::new) // Convert VariableDTO -> ArgumentTableEntry
                    .toList();
            variableTable.getItems().setAll(entries); // Replace items in the table

            for (int i = 0; i < entries.size(); i++) {
                ArgumentTableEntry newEntry = entries.get(i);
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

    public ObservableBooleanValue isProgramLoaded() {
        return isProgramLoaded;
    }

    public BooleanProperty isProgramLoadedProperty() {
        return isProgramLoaded;
    }

    public void updateCycles() {
        model.getProgramData().ifPresent(programData ->
                currentCycles.set(programData.getCurrentCycles()));
    }

    void updateAfterDebugStep() {
        model.getProgramData().ifPresent(model-> {
            nextInstructionIdForDebug.set(model.getNextInstructionIdForDebug());
            if (!model.isDebugmode()){ // Debugging finished
                nextInstructionIdForDebug.set(0);
                leftController.clearMarkInInstructionTable();

            }
            leftController.markEntryInInstructionTable(nextInstructionIdForDebug.get()-1);
        });
        updateResultVariableTable();
        updateIsDebugProperty();
        updateCycles();
    }

    public void updateIsDebugProperty() {
        if (model.isProgramLoaded()) {
            model.getProgramData().ifPresent(data ->
                    isDebugMode.set(data.isDebugmode())
            );
        } else {
            isDebugMode.set(false);
        }
    }

    public BooleanProperty isInDebugModeProperty() {
        return isDebugMode;
    }

    public void clearVariableTable() {
        variableTable.getItems().clear();
    }
}