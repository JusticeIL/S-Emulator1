package controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.InstructionTableEntry;

import java.util.List;
import java.util.stream.IntStream;

public class LeftSideController {

    private RightSideController rightController;
    private TopComponentController topController;
    private SingleProgramController model;
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(-1);
    private IntegerProperty maxLevel = new SimpleIntegerProperty(-1);

    @FXML
    private InstructionTableController chosenInstructionHistoryTableController;

    @FXML
    private InstructionTableController instructionTableController;

    @FXML
    private TableView<InstructionTableEntry> ChosenInstructionHistoryTable;

    @FXML
    private MenuButton functionChooser;

    @FXML
    private MenuButton expansionLevelMenu;

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

        expansionLevelMenu.disableProperty().bind(
                Bindings.isEmpty(expansionLevelMenu.getItems())
                        .or(rightController.isInDebugModeProperty())
        );
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
    public void initialize() {

        instructionsTable.setRowFactory(tv -> {
            TableRow<InstructionTableEntry> row = new TableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> e.consume());
            row.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> e.consume());
            row.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> e.consume());
            return row;
        });

        // Initialize the menu button of the expansion levels
        expansionLevelMenu.getItems().clear();
        maxLevel.addListener((obs, oldValue, newValue) -> {
            updateAvailableExpansionLevels(newValue.intValue());
        });

        // Initialize the highlight selection menu button
        highlightSelection.getItems().clear();
        highlightSelection.disableProperty().bind(Bindings.isEmpty(highlightSelection.getItems()));

        // Initialize the program or function selectin menu button
        functionChooser.getItems().clear();
        functionChooser.disableProperty().bind(Bindings.isEmpty(functionChooser.getItems()));
    }

    public void markEntryInInstructionTable(int entryId) {

        instructionsTable.getSelectionModel().clearAndSelect(entryId);
        instructionsTable.getFocusModel().focus(entryId);
        instructionsTable.scrollTo(entryId);
    }

    public void clearMarkInInstructionTable() {
        instructionsTable.getSelectionModel().clearSelection();
    }

    public void updateAvailableExpansionLevels(int maxLevel) {
        expansionLevelMenu.getItems().clear();
        IntStream.rangeClosed(0, maxLevel)
                .mapToObj(i -> {
                    MenuItem menuItem = new MenuItem(String.valueOf(i));
                    menuItem.setUserData(i);
                    menuItem.setOnAction((ActionEvent event) -> {
                        int selectedLevel = (int) menuItem.getUserData();
                        setCurrentLevel(selectedLevel);
                        expansionLevelMenu.setText(String.valueOf(i));
                        model.Expand(currentLevel.get());
                        updateMainInstructionTable();
                    });
                    return menuItem;
                })
                .forEach(expansionLevelMenu.getItems()::add);
    }
}