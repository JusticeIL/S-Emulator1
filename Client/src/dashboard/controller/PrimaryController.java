package dashboard.controller;

import controller.SingleProgramController;
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
        SingleProgramController singleProgramController = new SingleProgramController();
        leftSideController.setRightController(rightSideController);
        leftSideController.setTopController(topComponentController);
        rightSideController.setLeftController(leftSideController);
        rightSideController.setTopController(topComponentController);
        topComponentController.setLeftController(leftSideController);
        topComponentController.setRightController(rightSideController);
        topComponentController.setModel(singleProgramController);
    }

    public TopComponentController getTopComponentController() {
        return topComponentController;
    }
}