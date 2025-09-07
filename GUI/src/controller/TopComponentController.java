package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class TopComponentController extends HBox {

    private BorderPane mainController;

    public void setMainController(BorderPane mainController) {
        this.mainController = mainController;
    }

    @FXML
    private TextField currentLoadedProgramPath;

    @FXML
    private Button loadFileBtn;

    @FXML
    void loadProgramPressed(ActionEvent event) {

    }
}