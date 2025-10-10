package dashboard.controller;

import javafx.beans.binding.Bindings;
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
    private Button RerunBtn;

    @FXML
    private Button ShowStatisticsBtn;

    @FXML
    private TableView<?> usersTable;

    @FXML
    public void initialize() {
        unselectUserBtn.disableProperty().bind(Bindings.isEmpty(usersTable.getItems()));
        RerunBtn.disableProperty().bind(Bindings.isEmpty(userExecutionsTable.getItems()));
        ShowStatisticsBtn.disableProperty().bind(Bindings.isEmpty(userExecutionsTable.getItems()));
    }

    @FXML
    void unselectUser(ActionEvent event) {

    }

    @FXML
    void RerunPressed(ActionEvent event) {

    }

    @FXML
    void ShowStatisticsPressed(ActionEvent event) {

    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;
    }
}