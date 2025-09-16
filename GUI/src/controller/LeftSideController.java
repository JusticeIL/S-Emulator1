package controller;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.InstructionTableEntry;
import program.data.InstructionDTO;
import program.data.Searchable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeftSideController {

    private RightSideController rightController;
    private TopComponentController topController;
    private SingleProgramController model;
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(-1);
    private IntegerProperty maxLevel = new SimpleIntegerProperty(-1);
    private final int HISTORY_CHAIN_EFFECT_DURATION = 300; // milliseconds

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

        highlightSelection.disableProperty().bind(
                Bindings.isEmpty(highlightSelection.getItems())
                        .or(rightController.isInDebugModeProperty())
        );

        expansionLevelMenu.disableProperty().bind(
                Bindings.isEmpty(expansionLevelMenu.getItems())
                        .or(rightController.isInDebugModeProperty())
        );

        // Bind the selection of an instruction in the left table to "if in debug mode" in right controller
        instructionsTable.addEventFilter(MouseEvent.ANY, event -> {
            if (rightController.isInDebugModeProperty().get()) {
                event.consume();
            }
        });

        summaryLine.textProperty().bind(
                Bindings.when(rightController.isProgramLoaded())
                        .then(Bindings.createStringBinding(() ->
                                        "Program contains " +
                                                instructionsTable.getItems().stream().filter(entry -> "B".equals(entry.getType())).count() +
                                                " Basic Instructions, and " +
                                                instructionsTable.getItems().stream().filter(entry -> "S".equals(entry.getType())).count() +
                                                " Synthetic Instructions",
                                instructionsTable.getItems()
                        ))
                        .otherwise("No program loaded.")
        );
    }

    public void updateMainInstructionTable() {
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
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    updateParentInstructionTable(row.getItem().getInstructionDTO());
                }
            });
            return row;
        });

        // Initialize the menu button of the expansion levels
        expansionLevelMenu.getItems().clear();
        maxLevel.addListener((obs, oldValue, newValue) -> {
            updateAvailableExpansionLevels(newValue.intValue());
        });

        // Initialize the highlight selection menu button
        highlightSelection.getItems().clear();

        // Initialize the program or function selectin menu button
        functionChooser.getItems().clear();
        functionChooser.disableProperty().bind(Bindings.isEmpty(functionChooser.getItems()));

        // Initialize the history chain table and disable mouse interaction
        ChosenInstructionHistoryTable.setRowFactory(tv -> {
            TableRow<InstructionTableEntry> row = new TableRow<>() {};
            row.addEventFilter(MouseEvent.ANY, Event::consume);
            return row;
        });

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
                    Label label = new Label(String.valueOf(i));
                    label.setMaxWidth(Double.MAX_VALUE);
                    label.setStyle("-fx-alignment: center;");
                    CustomMenuItem menuItem = new CustomMenuItem(label, true);
                    menuItem.setUserData(i);
                    label.prefWidthProperty().bind(expansionLevelMenu.widthProperty());
                    menuItem.setOnAction((ActionEvent event) -> {
                        int selectedLevel = (int) menuItem.getUserData();
                        setCurrentLevel(selectedLevel);
                        expansionLevelMenu.setText(String.valueOf(i));
                        model.Expand(currentLevel.get());
                        updateMainInstructionTable();
                        updateVariablesOrLabelSelectionMenu();
                    });
                    return menuItem;
                })
                .forEach(expansionLevelMenu.getItems()::add);
    }

    public void resetLevelExpansionButtonText() {
        expansionLevelMenu.setText("0");
    }


    public void updateVariablesOrLabelSelectionMenu() {
        Set<Searchable> searchables = new HashSet<>();
        highlightSelection.getItems().clear();
        instructionsTable.getItems().forEach(entry -> {
            searchables.addAll(entry.getSearchables().stream().filter(searchable -> !Objects.equals(searchable.getName(), "    ")).collect(Collectors.toSet()));
        });

        List<Searchable> sortedSearchables = searchables.stream()
                .sorted(Comparator
                        .comparingInt((Searchable s) -> {
                            String name = s.getName();
                            if (name.startsWith("y")) return 1;
                            if (name.startsWith("x")) return 2;
                            if (name.startsWith("z")) return 3;
                            if (name.startsWith("L")) return 4;
                            return 5; // everything else goes last
                        })
                        .thenComparingInt(s -> {
                            String name = s.getName().substring(1); // drop prefix
                            try {
                                return Integer.parseInt(name);
                            } catch (NumberFormatException e) {
                                return Integer.MAX_VALUE; // non-numeric goes last
                            }
                        })
                )
                .toList();

        sortedSearchables.forEach(searchable -> {
            Label label = new Label(searchable.getName());
            label.setMaxWidth(Double.MAX_VALUE);
            label.setStyle("-fx-alignment: center;");
            CustomMenuItem Choice = new CustomMenuItem(label, true);
            Choice.setUserData(searchable.getName());
            label.prefWidthProperty().bind(highlightSelection.widthProperty());
            Choice.setOnAction((ActionEvent event) -> {
                instructionsTable.getSelectionModel().clearSelection();
                instructionsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                instructionsTable.getItems().stream().filter(inst -> inst.getSearchables().stream()
                        .anyMatch(s -> s.getName().equals(searchable.getName()))
                ).forEach(inst -> {
                    instructionsTable.getSelectionModel().select(inst);
                });
            });
            highlightSelection.getItems().add(Choice);
        });
    }

    private void updateParentInstructionTable(InstructionDTO instruction) {
        List<InstructionTableEntry> historyEntries = new ArrayList<>();
        InstructionDTO parent = instruction.getParentInstruction();
        while (parent != null) {
            historyEntries.add(new InstructionTableEntry(parent));
            parent = parent.getParentInstruction();
        }

        // Clear the table first
        ChosenInstructionHistoryTable.getItems().clear();
        ChosenInstructionHistoryTable.getItems().addAll(historyEntries);

        // Run after layout to ensure rows are created
        List<Node> rows = new ArrayList<>(ChosenInstructionHistoryTable.lookupAll(".table-row-cell"));
        rows.forEach(row -> row.setOpacity(0));
        int delay = 0;
        for (Node row : rows) {
            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            pause.setOnFinished(e -> {
                FadeTransition fade = new FadeTransition(Duration.millis(200), row);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
            });
            pause.play();
            delay += HISTORY_CHAIN_EFFECT_DURATION;
        }
    }

    public void clearHistoryChainTable() {
        ChosenInstructionHistoryTable.getItems().clear();
    }
}