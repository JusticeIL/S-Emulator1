package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class TopComponentController{

    private Stage primaryStage;
    private RightSideController rightController;
    private LeftSideController leftController;

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;
    }

    public void setLeftController(LeftSideController leftController) {
        this.leftController = leftController;
    }

    @FXML
    private TextField currentLoadedProgramPath;

    @FXML
    private Button loadFileBtn;

    @FXML
    void loadProgramPressed(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Program");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Program File", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();
    }
}