package controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableView;
import model.InstructionTableEntry;

import java.util.List;

public class LeftSideController {

    private RightSideController rightController;
    private TopComponentController topController;
    private SingleProgramController model;
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(-1);
    private final IntegerProperty maxLevel = new SimpleIntegerProperty(-1);

    @FXML InstructionTableController chosenInstructionHistoryTableController; // TODO: add access modifier
    @FXML InstructionTableController instructionTableController; // TODO: add access modifier

    @FXML
    private TableView<InstructionTableEntry> ChosenInstructionHistoryTable;

    @FXML
    private Button collapseBtn;

    @FXML
    private Label degreeRepresentationLabel;

    @FXML
    private Button expandBtn;

    @FXML
    private MenuButton highlightSelection;

    @FXML
    private TableView<InstructionTableEntry> instructionsTable;

    @FXML
    private Label summaryLine;

    public void setModel(SingleProgramController model) {
        this.model = model;
    }

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;
    }

    public void updateMainInstructionTable(){
        model.getProgramData().ifPresent(programData -> {
            List<InstructionTableEntry> entries = programData.getProgramInstructions().stream()
                    .map(InstructionTableEntry::new) // Convert InstructionDTO -> InstructionTableEntry
                    .toList();
            instructionsTable.getItems().setAll(entries);// Replace items in the table
        });
    }

    public void updateExpansionLevels() {
        model.getProgramData().ifPresent(programData -> {
            maxLevel.set(programData.getMaxExpandLevel());
        });
    }

    public void setCurrentLevel(int level) {
        currentLevel.set(level);
    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    @FXML
    private void initialize() {
        // Binding depends on currentLevel and maxLevel.
        degreeRepresentationLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            if (model == null || !model.isProgramLoaded()) {
                                return "Level: Curr/Max";
                            } else {
                                return "Level: " + currentLevel.get() + "/" + maxLevel.get();
                            }
                        },
                        currentLevel, maxLevel
                )
        );

        // Initialize button enabled/disabled state
        collapseBtn.disableProperty().bind(currentLevel.lessThanOrEqualTo(0));
        expandBtn.disableProperty().bind(currentLevel.greaterThanOrEqualTo(maxLevel));
    }

    @FXML
    void collapseCurrentProgram(ActionEvent event) {
        if (currentLevel.get() > 0) {
            currentLevel.set(currentLevel.get() - 1);
        }
        model.Expand(currentLevel.get());
        updateMainInstructionTable();
    }

    @FXML
    void expandCurrentProgram(ActionEvent event) {
        if (currentLevel.get() < maxLevel.get()) {
            currentLevel.set(currentLevel.get() + 1);
        }
        model.Expand(currentLevel.get());
        updateMainInstructionTable();
    }

    public void markEntryInInstructionTable(int entryId) {

        instructionsTable.getSelectionModel().clearAndSelect(entryId);
        instructionsTable.getFocusModel().focus(entryId);
        instructionsTable.scrollTo(entryId);
    }
}