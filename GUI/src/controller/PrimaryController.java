package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import program.data.ProgramData;

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
        leftSideController.setModel(singleProgramController);
        rightSideController.setModel(singleProgramController);
        topComponentController.setModel(singleProgramController);
    }
}
