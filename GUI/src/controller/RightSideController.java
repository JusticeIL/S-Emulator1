package controller;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import model.ArgumentTableEntry;
import model.HistoryTableEntry;
import program.data.VariableDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RightSideController{

    private TopComponentController topController;
    private LeftSideController leftController;
    private SingleProgramController model;
    private final IntegerProperty historySizeProperty = new SimpleIntegerProperty(0);
    private final BooleanProperty isDebugMode = new SimpleBooleanProperty(false);
    private final BooleanProperty isProgramLoaded = new SimpleBooleanProperty(false);

    private final SimpleIntegerProperty nextInstructionIdForDebug = new SimpleIntegerProperty(0);

    public void setModel(SingleProgramController model) {

        this.model = model;
        isProgramLoaded.set(model.isProgramLoaded());
    }

    public void OnProgramLoaded() {
        isProgramLoaded.set(true);
    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void setLeftController(LeftSideController leftController) {
        this.leftController = leftController;
    }

    @FXML
    private TableView<ArgumentTableEntry> executionArgumentInput;

    @FXML
    private Button RerunBtn;

    @FXML
    private Button ResumeDebugBtn;

    @FXML
    private Button RunProgramBtn;

    @FXML
    private Button StartDebugBtn;

    @FXML
    private TableColumn<ArgumentTableEntry, String> argumentNamesColumn;

    @FXML
    private TableColumn<ArgumentTableEntry, Number> argumentValuesColumn;

    @FXML
    private TableView<HistoryTableEntry> StatisticsTable;

    @FXML
    private TableColumn<HistoryTableEntry, Number> runNumberColumn;

    @FXML
    private TableColumn<HistoryTableEntry, Number> expansionLevelColumn;

    @FXML
    private TableColumn<HistoryTableEntry, String> xInputArgumentsColumn;

    @FXML
    private TableColumn<HistoryTableEntry, Number> yOutputColumn;

    @FXML
    private TableColumn<HistoryTableEntry, Number> cyclesConsumedColumn;

    @FXML
    private Button ShowStatisticsBtn;

    @FXML
    private Button StepOverDebugBtn;

    @FXML
    private Button SetUpRunBtn;

    @FXML
    private Button StopDebugBtn;

    @FXML
    private TableView<ArgumentTableEntry> variableTable;

    @FXML
    private TableColumn<ArgumentTableEntry, String> resultVariableNameCollumn;

    @FXML
    private TableColumn<ArgumentTableEntry,Number> resultVariableValueCollumn;

    @FXML
    private Label cyclesLabel;

    public void loadArguments(List<String> xArgumentsList) { // TODO: implement
        ;
    }

    @FXML
    public void initialize() {
        argumentNamesColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        argumentValuesColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        resultVariableNameCollumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        resultVariableValueCollumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        argumentValuesColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        argumentValuesColumn.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.NumberStringConverter()));

        // Update underlying model when editing finishes
        argumentValuesColumn.setOnEditCommit(event -> {
            ArgumentTableEntry entry = event.getRowValue();
            entry.valueProperty().set(event.getNewValue().intValue());
        });

        // Allow editing only on values
        executionArgumentInput.setEditable(true);
        executionArgumentInput.getSelectionModel().setCellSelectionEnabled(true);

        // Statistics Table columns initializing
        runNumberColumn.setCellValueFactory(new PropertyValueFactory<>("run"));
        expansionLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        xInputArgumentsColumn.setCellValueFactory(new PropertyValueFactory<>("args"));
        yOutputColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        cyclesConsumedColumn.setCellValueFactory(new PropertyValueFactory<>("cycles"));

        historySizeProperty.addListener((obs, oldSize, newSize) -> updateStatisticsTable());

        RerunBtn.disableProperty().bind(
                Bindings.isEmpty(StatisticsTable.getItems())
        );
        ShowStatisticsBtn.disableProperty().bind(
                Bindings.isEmpty(StatisticsTable.getItems())
        );
        StepOverDebugBtn.disableProperty().bind(isDebugMode.not());
        ResumeDebugBtn.disableProperty().bind(isDebugMode.not());
        StopDebugBtn.disableProperty().bind(isDebugMode.not());

        StartDebugBtn.disableProperty().bind(
                isProgramLoaded.not().or(isDebugMode)
        );

        SetUpRunBtn.disableProperty().bind(
                isProgramLoaded.not().or(isDebugMode)
        );

        RunProgramBtn.disableProperty().bind(
                isProgramLoaded.not().or(isDebugMode)
        );




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
            List<ArgumentTableEntry> entries = programData.getProgramVariablesCurrentState().stream()
                    .map(ArgumentTableEntry::new) // Convert VariableDTO -> ArgumentTableEntry
                    .toList();
            variableTable.getItems().setAll(entries);// Replace items in the table
        })
        ;
    }

    public void updateStatisticsTable() {
        model.getProgramData().ifPresent(programData -> {
            List<HistoryTableEntry> entries = programData.getStatistics().getHistory().stream()
                    .map(HistoryTableEntry::new) // Convert Run -> HistoryTableEntry
                    .toList();
            StatisticsTable.getItems().setAll(entries);// Replace items in the table
        })
        ;
    }

    public void clearStatisticsTable() {
        StatisticsTable.getItems().clear();
        StatisticsTable.getSortOrder().clear();
        StatisticsTable.getSelectionModel().clearSelection();
        historySizeProperty.set(0);
    }

    public void refreshHistorySize() {
        model.getProgramData().ifPresent(programData -> {
            int newSize = programData.getStatistics().getHistory().size();
            historySizeProperty.set(newSize);
        });
    }

    @FXML
    void ExecutionArgumentUpdated(ActionEvent event) { // TODO: implement

    }

    @FXML
    void RerunPressed(ActionEvent event) {
        List<ArgumentTableEntry> argsList = new ArrayList<>();

        String argsString = StatisticsTable.getSelectionModel().getSelectedItem().getArgs();
        if (argsString.startsWith("[") && argsString.endsWith("]")) {
            argsString = argsString.substring(1, argsString.length() - 1); // strip [ ]
        }

        String[] pairs = argsString.split(", ");
        for (String pair : pairs) {
            String[] kv = pair.split(" = ");
            if (kv.length == 2) {
                String name = kv[0].trim();
                int value = Integer.parseInt(kv[1].trim());
                argsList.add(new ArgumentTableEntry(name) {{
                    setValue(value);
                }});
            }
        }
        executionArgumentInput.getItems().setAll(argsList);
        model.Expand(StatisticsTable.getSelectionModel().getSelectedItem().getLevel());
        leftController.updateMainInstructionTable();
        leftController.setCurrentLevel(StatisticsTable.getSelectionModel().getSelectedItem().getLevel());
        variableTable.getItems().clear();
    }

    @FXML
    void ResumeDebugPressed(ActionEvent event) {

    }

    @FXML
    void RunProgramPressed(ActionEvent event) {
        Set<VariableDTO> argumentValues = executionArgumentInput.getItems().stream()
                .map(entry-> new VariableDTO(entry.getName(), entry.getValue())) // ArgumentTableEntry -> VariableDTO
                .collect(Collectors.toSet());
        // Pass them to runProgram
        model.runProgram(argumentValues);
        updateResultVariableTable();

        refreshHistorySize();
    }


    @FXML
    void ShowStatisticsPressed(ActionEvent event) {

    }

    @FXML
    void StartDebugPressed(ActionEvent event) {
        Set<VariableDTO> argumentValues = executionArgumentInput.getItems().stream()
                .map(entry-> new VariableDTO(entry.getName(), entry.getValue())) // ArgumentTableEntry -> VariableDTO
                .collect(Collectors.toSet());
        model.startDebug(argumentValues);
        model.getProgramData().ifPresent(model->nextInstructionIdForDebug.set(model.getNextInstructionIdForDebug()));
        leftController.markEntryInInstructionTable(nextInstructionIdForDebug.get()-1);
        updateResultVariableTable();
        updateIsDebugProperty();
    }

    @FXML
    void StepOverDebugPressed(ActionEvent event) {
        model.stepOver();
        model.getProgramData().ifPresent(model-> {
            nextInstructionIdForDebug.set(model.getNextInstructionIdForDebug());
            if(!model.isDebugmode()){
                // Debugging finished
                nextInstructionIdForDebug.set(0);
                leftController.clearMarkInInstructionTable();
            }
            updateStatisticsTable();
            leftController.markEntryInInstructionTable(nextInstructionIdForDebug.get()-1);
        });
        updateResultVariableTable();
        updateIsDebugProperty();
    }

    @FXML
    void StopDebugPressed(ActionEvent event) {
        model.StopDebug();
        updateIsDebugProperty();
        updateStatisticsTable();
        leftController.clearMarkInInstructionTable();
    }

    private void updateIsDebugProperty(){
        if ( model.isProgramLoaded()) {
            model.getProgramData().ifPresent(data ->
                    isDebugMode.set(data.isDebugmode())
            );
        } else {
            isDebugMode.set(false);
        }
    }

    @FXML
    void SetupNewRunPressed(ActionEvent event) {
        variableTable.getItems().clear();
        executionArgumentInput.getItems().forEach(entry->{entry.valueProperty().set(0);});
    }

}