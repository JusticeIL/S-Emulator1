package controller;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import model.ArgumentTableEntry;
import model.InstructionTableEntry;

import java.util.List;

public class RightSideController{

    private TopComponentController topController;
    private LeftSideController leftController;
    private SingleProgramController model;


    public void setModel(SingleProgramController model) {
        this.model = model;
    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void setLeftController(LeftSideController leftController) {
        this.leftController = leftController;
    }

    @FXML
    private TableView<ArgumentTableEntry> ExecutionArgumentInput;


    @FXML
    private Button RerunBtn;

    @FXML
    private Button ResumeDebugBtn;

    @FXML
    private Button RunProgramBtn;

    @FXML
    private TableColumn<ArgumentTableEntry, String> argumentNamesColumn;

    @FXML
    private TableColumn<ArgumentTableEntry, Number> argumentValuesColumn;


    @FXML
    private Button ShowStatisticsBtn;

    @FXML
    private Button StartDebugBtn;

    @FXML
    private TableView<?> StatisticsTable;

    @FXML
    private Button StepOverDebugBtn;

    @FXML
    private Button StopDebugBtn;

    @FXML
    private TableView<?> VariableTable;

    @FXML
    private Label cyclesLabel;

    public void loadArguments(List<String> xArgumentsList) { // TODO: implement
        ;
    }

    @FXML
    public void initialize() { // TODO: initialize table columns and avoid NPE
        argumentNamesColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        argumentValuesColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        argumentValuesColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        argumentValuesColumn.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.NumberStringConverter()));

        // Update underlying model when editing finishes
        argumentValuesColumn.setOnEditCommit(event -> {
            ArgumentTableEntry entry = event.getRowValue();
            entry.valueProperty().set(event.getNewValue().intValue());
        });

        // Allow editing only on values
        ExecutionArgumentInput.setEditable(true);
        ExecutionArgumentInput.getSelectionModel().setCellSelectionEnabled(true);

    }

    public void updateArgumentTable() {
        model.getProgramData().ifPresent(programData -> {
            List<ArgumentTableEntry> entries = programData.getProgramXArguments().stream()
                    .map(ArgumentTableEntry::new) // Convert ArgumentDTO -> ArgumentTableEntry
                    .toList();
            ExecutionArgumentInput.getItems().setAll(entries);// Replace items in the table
        })
        ;
    }

    @FXML
    void ExecutionArgumentUpdated(ActionEvent event) { // TODO: implement

    }

    @FXML
    void RerunPressed(ActionEvent event) {

    }

    @FXML
    void ResumeDebugPressed(ActionEvent event) {

    }

    @FXML
    void RunProgramPressed(ActionEvent event) {

    }

    @FXML
    void ShowStatisticsPressed(ActionEvent event) {

    }

    @FXML
    void StartDebugPressed(ActionEvent event) {

    }

    @FXML
    void StepOverDebugPressed(ActionEvent event) {

    }

    @FXML
    void StopDebugPressed(ActionEvent event) {

    }

}
