package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import program.data.ProgramData;

public class PrimaryController
{

    @FXML private LeftSideController leftSideController;
    @FXML private RightSideController rightSideController;
    @FXML private TopComponentController topComponentController;

    SingleProgramController model;
    ProgramData lastLoadedProgramData;

    public SingleProgramController getModel() {
        return model;
    }

    public void setModel(SingleProgramController model) {
        this.model = model;
    }

    public ProgramData getLastLoadedProgramData() {
        return lastLoadedProgramData;
    }

    public void setLastLoadedProgramData(ProgramData lastLoadedProgramData) {
        this.lastLoadedProgramData = lastLoadedProgramData;
    }

    @FXML void initialize() {
        leftSideController.setRightController(rightSideController);
        leftSideController.setTopController(topComponentController);
        rightSideController.setLeftController(leftSideController);
        rightSideController.setTopController(topComponentController);
        topComponentController.setLeftController(leftSideController);
        topComponentController.setRightController(rightSideController);
    }
}
