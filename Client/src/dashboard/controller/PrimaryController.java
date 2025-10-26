package dashboard.controller;

import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private LeftSideController leftSideController;

    @FXML
    private RightSideController rightSideController;

    @FXML
    private TopComponentController topComponentController;

    @FXML
    public void initialize() {
        leftSideController.setTopController(topComponentController);
    }

    public TopComponentController getTopComponentController() {
        return topComponentController;
    }
}