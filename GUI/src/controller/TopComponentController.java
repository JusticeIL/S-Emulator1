package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class TopComponentController extends HBox {

    private BorderPane mainController;
    private Stage primaryStage;

    public void setMainController(BorderPane mainController) {
        this.mainController = mainController;
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