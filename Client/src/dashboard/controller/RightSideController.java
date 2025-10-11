package dashboard.controller;

import controller.SingleProgramController;
import dashboard.model.FunctionTableEntry;
import dashboard.model.ProgramTableEntry;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.OkHttpClient;

public class RightSideController {

    private TopComponentController topController;
    private LeftSideController leftController;
    private SingleProgramController model;
    private OkHttpClient client;

    @FXML
    private TableView<ProgramTableEntry> programsTable;

    @FXML
    private TableColumn<ProgramTableEntry, String> programNameColumn;

    @FXML
    private TableColumn<ProgramTableEntry, String> userProgramOriginColumn;

    @FXML
    private TableColumn<ProgramTableEntry, Integer> instructionsCounterProgramColumn;

    @FXML
    private TableColumn<ProgramTableEntry, Integer> programMaxLevelColumn;

    @FXML
    private TableColumn<ProgramTableEntry, Integer> executionsCounterColumn;

    @FXML
    private TableColumn<ProgramTableEntry, Number> averageCostColumn;

    @FXML
    private Button executeProgramBtn;

    @FXML
    private TableView<FunctionTableEntry> functionsTable;

    @FXML
    private TableColumn<FunctionTableEntry, String> functionNameColumn;

    @FXML
    private TableColumn<FunctionTableEntry, String> programOriginColumn;

    @FXML
    private TableColumn<FunctionTableEntry, String> userFunctionOriginColumn;

    @FXML
    private TableColumn<FunctionTableEntry, Integer> instructionsCounterFunctionColumn;

    @FXML
    private TableColumn<FunctionTableEntry, Integer> functionMaxLevelColumn;

    @FXML
    private Button executeFunctionBtn;

    @FXML
    public void initialize() {
        executeProgramBtn.disableProperty().bind(Bindings.isEmpty(programsTable.getItems()));
        executeFunctionBtn.disableProperty().bind(Bindings.isEmpty(functionsTable.getItems()));

        /* Tables */
        programsTable.setRowFactory(tv -> new TableRow<>());

        programNameColumn.setCellValueFactory(new PropertyValueFactory<>("programName"));
        userProgramOriginColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        instructionsCounterProgramColumn.setCellValueFactory(new PropertyValueFactory<>("instructionsCounter"));
        programMaxLevelColumn.setCellValueFactory(new PropertyValueFactory<>("maxProgramLevel"));
        executionsCounterColumn.setCellValueFactory(new PropertyValueFactory<>("executionsCounter"));
        averageCostColumn.setCellValueFactory(new PropertyValueFactory<>("averageExecutionCost"));

        functionsTable.setRowFactory(tv -> new TableRow<>());

        functionNameColumn.setCellValueFactory(new PropertyValueFactory<>("functionName"));
        programOriginColumn.setCellValueFactory(new PropertyValueFactory<>("programOrigin"));
        userFunctionOriginColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        instructionsCounterFunctionColumn.setCellValueFactory(new PropertyValueFactory<>("instructionsCounter"));
        functionMaxLevelColumn.setCellValueFactory(new PropertyValueFactory<>("maxProgramLevel"));
    }

    @FXML
    void executeFunction(ActionEvent event) {

    }

    @FXML
    void executeProgram(ActionEvent event) {

    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void setLeftController(LeftSideController leftController) {
        this.leftController = leftController;
    }
}