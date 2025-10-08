package dashboard.controller;

import controller.SingleProgramController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

public class RightSideController {

    private TopComponentController topController;
    private LeftSideController leftController;
    private SingleProgramController model;

    @FXML
    private Button executeFunctionBtn;

    @FXML
    private Button executeProgramBtn;

    @FXML
    private TableView<?> functionsTable;

    @FXML
    private TableView<?> programsTable;

    @FXML
    public void initialize() {

    }

    @FXML
    void executeFunction(ActionEvent event) {

    }

    @FXML
    void executeProgram(ActionEvent event) {

    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void setLeftController(LeftSideController leftController) {
        this.leftController = leftController;
    }
}