package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import model.InstructionTableEntry;

import java.util.List;

public class LeftSideController {

    private RightSideController rightController;
    private TopComponentController topController;
    private SingleProgramController model;

    @FXML InstructionTableController chosenInstructionHistoryTableController;
    @FXML InstructionTableController instructionTableController;



    public void setModel(SingleProgramController model) {
        this.model = model;
    }

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;
    }

    public void updateMainInstructionTable(){
        model.getProgramData().ifPresent(programData -> {
            List<InstructionTableEntry> entries = programData.getProgramInstructions().stream()
                    .map(InstructionTableEntry::new) // convert InstructionDTO -> InstructionTableEntry
                    .toList();
            instructionsTable.getItems().setAll(entries);// replace items in the table
        });
    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    @FXML
    private TableView<InstructionTableEntry> ChosenInstructionHistoryTable;

    @FXML
    private Button collapseBtn;

    @FXML
    private Label degreeRepresentationBtn;

    @FXML
    private Button expandBtn;

    @FXML
    private MenuButton highlightSelection;

    @FXML
    private TableView<InstructionTableEntry> instructionsTable;

    @FXML
    private Label summaryLine;

    @FXML
    void collapseCurrentProgram(ActionEvent event) {

    }

    @FXML
    void expandCurrentProgram(ActionEvent event) {

    }

}