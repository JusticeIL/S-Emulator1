package dashboard.controller;

import controller.SingleProgramController;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import okhttp3.OkHttpClient;

public class RightSideController {

    private TopComponentController topController;
    private LeftSideController leftController;
    private SingleProgramController model;
    private OkHttpClient client;

    @FXML
    private TableView<?> programsTable;

    @FXML
    private Button executeProgramBtn;

    @FXML
    private TableView<?> functionsTable;

    @FXML
    private Button executeFunctionBtn;

    @FXML
    public void initialize() {
        executeProgramBtn.disableProperty().bind(Bindings.isEmpty(programsTable.getItems()));
        executeFunctionBtn.disableProperty().bind(Bindings.isEmpty(functionsTable.getItems()));
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