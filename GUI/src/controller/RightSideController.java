package controller;

import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;
import model.ArgumentTableEntry;
import model.HistoryTableEntry;
import model.InstructionTableEntry;
import program.data.VariableDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RightSideController{

    private TopComponentController topController;
    private LeftSideController leftController;
    private Stage primaryStage;
    private SingleProgramController model;
    private boolean isShowHistoryDialogOpen = false;
    private final IntegerProperty historySizeProperty = new SimpleIntegerProperty(0);
    private final BooleanProperty isDebugMode = new SimpleBooleanProperty(false);
    private final BooleanProperty isProgramLoaded = new SimpleBooleanProperty(false);
    private final IntegerProperty currentCycles = new SimpleIntegerProperty(-1);
    private final SimpleIntegerProperty nextInstructionIdForDebug = new SimpleIntegerProperty(0);

    @FXML
    private TableView<ArgumentTableEntry> executionArgumentInput;

    @FXML
    private Button RerunBtn;

    @FXML
    private Button ResumeDebugBtn;

    @FXML
    private Button RunProgramBtn;

    @FXML
    private TableColumn<ArgumentTableEntry, String> argumentNamesColumn;

    @FXML
    private TableColumn<ArgumentTableEntry, Number> argumentValuesColumn;

    @FXML
    private TableView<HistoryTableEntry> StatisticsTable;

    @FXML
    private TableColumn<HistoryTableEntry, Number> runNumberColumn;

    @FXML
    private TableColumn<HistoryTableEntry, Number> expansionLevelColumn;

    @FXML
    private TableColumn<HistoryTableEntry, Number> yOutputColumn;

    @FXML
    private TableColumn<HistoryTableEntry, Number> cyclesConsumedColumn;

    @FXML
    private Button ShowStatisticsBtn;

    @FXML
    private Button StepOverDebugBtn;

    @FXML
    private Button SetUpRunBtn;

    @FXML
    private RadioButton debugRadioButton;

    @FXML
    private RadioButton runRadioButton;

    @FXML
    private Button StopDebugBtn;

    @FXML
    private TableView<ArgumentTableEntry> variableTable;

    @FXML
    private TableColumn<ArgumentTableEntry, String> resultVariableNameColumn;

    @FXML
    private TableColumn<ArgumentTableEntry,Number> resultVariableValueColumn;

    @FXML
    private Label cyclesLabel;

    @FXML
    public void initialize() {
        argumentNamesColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        argumentValuesColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        resultVariableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        resultVariableValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        argumentValuesColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        argumentValuesColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));

        // Update underlying model when editing finishes
        argumentValuesColumn.setOnEditCommit(event -> {
            ArgumentTableEntry entry = event.getRowValue();
            entry.valueProperty().set(event.getNewValue().intValue());
        });

        variableTable.getSortOrder().clear();
        variableTable.setSortPolicy(param -> null); // disables sorting globally
        StatisticsTable.getSortOrder().clear();
        StatisticsTable.setSortPolicy(param -> null); // disables sorting globally
        executionArgumentInput.getSortOrder().clear();
        executionArgumentInput.setSortPolicy(param -> null); // disables sorting globally

        // Allow editing only on values
        executionArgumentInput.setEditable(true);
        executionArgumentInput.getSelectionModel().setCellSelectionEnabled(true);

        // Statistics Table columns initializing
        runNumberColumn.setCellValueFactory(new PropertyValueFactory<>("run"));
        expansionLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        yOutputColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        cyclesConsumedColumn.setCellValueFactory(new PropertyValueFactory<>("cycles"));

        historySizeProperty.addListener((obs, oldSize, newSize) -> updateStatisticsTable());

        /* Buttons initialization */
        // Rerun
        RerunBtn.disableProperty().bind(
                Bindings.isEmpty(StatisticsTable.getItems())
                        .or(Bindings.isEmpty(StatisticsTable.getSelectionModel().getSelectedItems()))
                        .or(isDebugMode)
        );

        // Radio buttons
        ToggleGroup modeToggleGroup = new ToggleGroup();
        debugRadioButton.setToggleGroup(modeToggleGroup);
        runRadioButton.setToggleGroup(modeToggleGroup);

        // Debugging buttons
        StepOverDebugBtn.disableProperty().bind(isDebugMode.not());
        ResumeDebugBtn.disableProperty().bind(isDebugMode.not());
        StopDebugBtn.disableProperty().bind(isDebugMode.not());

        SetUpRunBtn.disableProperty().bind(
                isProgramLoaded.not().or(isDebugMode)
        );

        RunProgramBtn.disableProperty().bind(
                isProgramLoaded.not().or(isDebugMode)
        );

        // Show statistics
        ShowStatisticsBtn.disableProperty().bind(
                Bindings.isEmpty(StatisticsTable.getItems()).or(Bindings.isEmpty(StatisticsTable.getSelectionModel().getSelectedItems()))
        );

        // Initialize the cycles label
        cyclesLabel.textProperty().bind(Bindings.createStringBinding(
                () -> (currentCycles.get() < 0) ? "Cycles: ---" : "Cycles: " + currentCycles.get(),
                currentCycles
        ));

        variableTable.placeholderProperty().bind(
                Bindings.when(isProgramLoaded.not())
                        .then(new Label("No program loaded."))
                        .otherwise(new Label("No variables state to present"))
        );
        variableTable.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, Event::consume); // Disable selection from user only

        StatisticsTable.placeholderProperty().bind(
                Bindings.when(isProgramLoaded.not())
                        .then(new Label("No program loaded."))
                        .otherwise(new Label("No runs have been executed for this program."))
        );

        // Minimize tables' height to prevent vertical scrolling
        variableTable.setPrefHeight(variableTable.getPrefHeight() * 0.85);
        executionArgumentInput.setPrefHeight(executionArgumentInput.getPrefHeight() * 0.85);
    }

    @FXML
    public void RerunPressed(ActionEvent event) {
        List<ArgumentTableEntry> argsList = new ArrayList<>();

        String argsString = StatisticsTable.getSelectionModel().getSelectedItem().getArgs();
        if (argsString.startsWith("[") && argsString.endsWith("]")) {
            argsString = argsString.substring(1, argsString.length() - 1); // strip [ ]
        }

        String[] pairs = argsString.split(", ");
        for (String pair : pairs) {
            String[] kv = pair.split(" = ");
            if (kv.length == 2) {
                String name = kv[0].trim();
                int value = Integer.parseInt(kv[1].trim());
                argsList.add(new ArgumentTableEntry(name) {{
                    setValue(value);
                }});
            }
        }
        executionArgumentInput.getItems().setAll(argsList);
        model.Expand(StatisticsTable.getSelectionModel().getSelectedItem().getLevel());
        leftController.updateMainInstructionTable();
        leftController.setCurrentLevel(StatisticsTable.getSelectionModel().getSelectedItem().getLevel());
        variableTable.getItems().clear();
    }

    @FXML
    public void ResumeDebugPressed(ActionEvent event) {
        model.resumeDebug();
        updateAfterDebugStep();
    }

    @FXML
    public void RunProgramPressed(ActionEvent event) {

        if (runRadioButton.isSelected()) {
            Set<VariableDTO> argumentValues = executionArgumentInput.getItems().stream()
                    .map(entry-> new VariableDTO(entry.getName(), entry.getValue())) // ArgumentTableEntry -> VariableDTO
                    .collect(Collectors.toSet());
            // Pass them to runProgram
            model.runProgram(argumentValues);
            updateResultVariableTable();

            refreshHistorySize();
            model.getProgramData().ifPresent(programData ->
                    currentCycles.set(programData.getCurrentCycles()));
        }
        else {
            StartDebugPressed(event);
        }
    }


    @FXML
    public void ShowStatisticsPressed(ActionEvent event) {
        if (!isShowHistoryDialogOpen && !StatisticsTable.getSelectionModel().isEmpty()) {
            isShowHistoryDialogOpen = true;
            HistoryTableEntry entry = StatisticsTable.getSelectionModel().getSelectedItem();
            Map<String, Integer> allEntryVariables = entry.getAllVariables();
            Stage dialogStage = createVariablesTableDialog(allEntryVariables);
            dialogStage.setOnCloseRequest(closeEvent -> isShowHistoryDialogOpen = false);
            dialogStage.show();
        }
    }

    @FXML
    void StartDebugPressed(ActionEvent event) {
        Set<VariableDTO> argumentValues = executionArgumentInput.getItems().stream()
                .map(entry-> new VariableDTO(entry.getName(), entry.getValue())) // ArgumentTableEntry -> VariableDTO
                .collect(Collectors.toSet());
        Set<Integer> breakpoints = leftController.getEntriesWithBreakpoints().stream()
                .map(InstructionTableEntry::getId).collect(Collectors.toSet());
        model.startDebug(argumentValues, breakpoints);
        model.getProgramData().ifPresent(model->nextInstructionIdForDebug.set(model.getNextInstructionIdForDebug()));
        leftController.markEntryInInstructionTable(nextInstructionIdForDebug.get()-1);
        updateResultVariableTable();
        updateIsDebugProperty();
        model.getProgramData().ifPresent(programData ->
                currentCycles.set(programData.getCurrentCycles()));
        leftController.clearHistoryChainTable(); // Clear history chain table on new debug start
        updateAfterDebugStep();
    }

    @FXML
    void StepOverDebugPressed(ActionEvent event) {
        model.stepOver();
        updateAfterDebugStep();
    }

    @FXML
    void StopDebugPressed(ActionEvent event) {
        model.stopDebug();
        updateIsDebugProperty();
        updateStatisticsTable();
        leftController.clearMarkInInstructionTable();
    }

    @FXML
    void SetupNewRunPressed(ActionEvent event) {
        variableTable.getItems().clear();
        executionArgumentInput.getItems().forEach(entry->{entry.valueProperty().set(0);});
        currentCycles.set(0);
    }

    public void setModel(SingleProgramController model) {

        this.model = model;
        isProgramLoaded.set(model.isProgramLoaded());
    }

    public void OnProgramLoaded() {
        updateCycles();
        isProgramLoaded.set(true);
    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setLeftController(LeftSideController leftController) {
        this.leftController = leftController;
    }

    public void updateArgumentTable() {
        model.getProgramData().ifPresent(programData -> {
            List<ArgumentTableEntry> entries = programData.getProgramXArguments().stream()
                    .map(ArgumentTableEntry::new) // Convert ArgumentDTO -> ArgumentTableEntry
                    .toList();
            executionArgumentInput.getItems().setAll(entries);// Replace items in the table
        })
        ;
    }

    public void updateResultVariableTable() {
        model.getProgramData().ifPresent(programData -> {
            List<ArgumentTableEntry> previousEntries = new ArrayList<>(variableTable.getItems());
            List<ArgumentTableEntry> entries = programData.getProgramVariablesCurrentState().stream()
                    .map(ArgumentTableEntry::new) // Convert VariableDTO -> ArgumentTableEntry
                    .toList();
            variableTable.getItems().setAll(entries); // Replace items in the table

            for (int i = 0; i < entries.size(); i++) {
                ArgumentTableEntry newEntry = entries.get(i);
                if (i < previousEntries.size()) {
                    ArgumentTableEntry oldEntry = previousEntries.get(i);
                    // Compare by value
                    if (newEntry.getValue() != oldEntry.getValue()) {
                        variableTable.getSelectionModel().select(i);
                    }
                    else {
                        variableTable.getSelectionModel().clearSelection(i);
                    }
                }
            }
        });
    }

    public void updateStatisticsTable() {
        model.getProgramData().ifPresent(programData -> {
            List<HistoryTableEntry> entries = programData.getStatistics().getHistory().stream()
                    .map(HistoryTableEntry::new) // Convert Run -> HistoryTableEntry
                    .toList();
            StatisticsTable.getItems().setAll(entries);// Replace items in the table
        })
        ;
    }

    public ObservableBooleanValue isProgramLoaded() {
        return isProgramLoaded;
    }

    public BooleanProperty isProgramLoadedProperty() {
        return isProgramLoaded;
    }

    public void clearStatisticsTable() {
        StatisticsTable.getItems().clear();
        StatisticsTable.getSortOrder().clear();
        StatisticsTable.getSelectionModel().clearSelection();
        historySizeProperty.set(0);
    }

    public void refreshHistorySize() {
        model.getProgramData().ifPresent(programData -> {
            int newSize = programData.getStatistics().getHistory().size();
            historySizeProperty.set(newSize);
        });
    }

    public void updateCycles(){
        model.getProgramData().ifPresent(programData ->
                currentCycles.set(programData.getCurrentCycles()));
    }

    void updateAfterDebugStep(){
        model.getProgramData().ifPresent(model-> {
            nextInstructionIdForDebug.set(model.getNextInstructionIdForDebug());
            if (!model.isDebugmode()){ // Debugging finished
                nextInstructionIdForDebug.set(0);
                leftController.clearMarkInInstructionTable();
                updateStatisticsTable();

            }
            leftController.markEntryInInstructionTable(nextInstructionIdForDebug.get()-1);
        });
        updateResultVariableTable();
        updateIsDebugProperty();
        model.getProgramData().ifPresent(programData ->
                currentCycles.set(programData.getCurrentCycles()));
    }

    public void updateIsDebugProperty(){
        if ( model.isProgramLoaded()) {
            model.getProgramData().ifPresent(data ->
                    isDebugMode.set(data.isDebugmode())
            );
        } else {
            isDebugMode.set(false);
        }
    }

    public BooleanProperty isInDebugModeProperty() {
        return isDebugMode;
    }

    public Stage createVariablesTableDialog(Map<String, Integer> allEntryVariables) {
        BorderPane root = new BorderPane();
        root.setPrefSize(600, 400);
        root.setOpacity(0);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BOTTOM_RIGHT);

        ColumnConstraints col = new ColumnConstraints();
        col.setHalignment(javafx.geometry.HPos.RIGHT);
        col.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        col.setMinWidth(10);
        col.setPrefWidth(100);
        grid.getColumnConstraints().add(col);

        RowConstraints row1 = new RowConstraints();
        row1.setMinHeight(10);
        row1.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
        RowConstraints row2 = new RowConstraints();
        row2.setMinHeight(50);
        row2.setMaxHeight(100);
        row2.setValignment(javafx.geometry.VPos.BOTTOM);
        row2.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
        grid.getRowConstraints().addAll(row1, row2);

        TableView<Object> tableView = new TableView<>();
        tableView.setEditable(true);
        tableView.setPrefSize(200, 200);
        tableView.setPadding(new Insets(20, 20, 50, 20));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setId("runAllVariablesTable");

        TableColumn<Object, String> colName = new TableColumn<>("Variable Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setMinWidth(50);
        TableColumn<Object, String> colValue = new TableColumn<>("Variable Value");
        colValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        colValue.setMinWidth(50);
        tableView.getColumns().addAll(colName, colValue);

        List<ArgumentTableEntry> sortedEntries = allEntryVariables.keySet().stream()
                .sorted((a, b) -> {
                    if (a.equals("y")) return -1;
                    if (b.equals("y")) return 1;
                    boolean aIsX = a.startsWith("x");
                    boolean bIsX = b.startsWith("x");
                    boolean aIsZ = a.startsWith("z");
                    boolean bIsZ = b.startsWith("z");
                    if (aIsX && bIsX) {
                        return Integer.compare(
                                Integer.parseInt(a.substring(1)),
                                Integer.parseInt(b.substring(1))
                        );
                    }
                    if (aIsZ && bIsZ) {
                        return Integer.compare(
                                Integer.parseInt(a.substring(1)),
                                Integer.parseInt(b.substring(1))
                        );
                    }
                    if (aIsX) return -1;
                    if (bIsX) return 1;
                    if (aIsZ) return -1;
                    if (bIsZ) return 1;
                    return a.compareTo(b);
                })
                .map(key -> new ArgumentTableEntry(new VariableDTO(key, allEntryVariables.get(key))))
                .toList();
        tableView.getItems().setAll(sortedEntries);

        Button closeBtn = new Button("✖ Close");
        closeBtn.setId("closeBtn");
        closeBtn.setAlignment(Pos.CENTER);
        GridPane.setMargin(closeBtn, new Insets(0, 20, 20, 0));
        GridPane.setRowIndex(closeBtn, 1);

        grid.add(tableView, 0, 0);
        grid.add(closeBtn, 0, 1);

        root.setCenter(grid);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(primaryStage.getScene().getStylesheets().getFirst());

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Variables table");
        dialogStage.setScene(scene);

        // Fade-in transition
        dialogStage.setOnShown(e -> {
            if (topController.isAnimationAllowedProperty().get()) { // Case: animation allowed
                FadeTransition fadeIn = new FadeTransition(Duration.millis(250), root);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            } else { // Case: user requests no animations
                root.setOpacity(1);
            }
        });

        // Fade-out transition on close
        closeBtn.setOnAction(e -> {
            isShowHistoryDialogOpen = false;
            if (topController.isAnimationAllowedProperty().get()) { // Case: animation allowed
                FadeTransition fadeOut = new FadeTransition(Duration.millis(250), root);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(ev -> dialogStage.close());
                fadeOut.play();
            } else { // Case: user requests no animations
                dialogStage.close();
            }
        });

        return dialogStage;
    }

    public void clearVariableTable() {
        variableTable.getItems().clear();
    }
}