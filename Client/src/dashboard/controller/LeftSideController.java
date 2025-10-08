package dashboard.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LeftSideController {

    private RightSideController rightController;
    private TopComponentController topController;

    @FXML
    private Button unselectUserBtn;

    @FXML
    private TableView<?> userExecutionsTable;

    @FXML
    private TableView<?> usersTable;

    @FXML
    public void initialize() {

    }

    @FXML
    void unselectUser(ActionEvent event) {

    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;
    }
}