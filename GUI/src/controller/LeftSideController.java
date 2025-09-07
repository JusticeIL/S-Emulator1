package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class LeftSideController extends BorderPane {

    private BorderPane mainController;

    public void setMainController(BorderPane mainController) {
        this.mainController = mainController;
    }

    @FXML
    private TableView<?> chosenInstructionHistoryTable;

    @FXML
    private Button collapseBtn;

    @FXML
    private Label degreeRepresentationBtn;

    @FXML
    private Button expandBtn;

    @FXML
    private MenuButton highlightSelection;

    @FXML
    private TableView<?> instructionTable;

    @FXML
    private Label summaryLine;

    @FXML
    void collapseCurrentProgram(ActionEvent event) {

    }

    @FXML
    void expandCurrentProgram(ActionEvent event) {

    }

}