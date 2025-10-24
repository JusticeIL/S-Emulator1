package dashboard.controller;

import dashboard.model.HistoryTableEntry;
import dashboard.model.UserTableEntry;
import dashboard.refreshTasks.HistoryTableRefresher;
import dashboard.refreshTasks.UserListRefresher;
import dto.ArchitectureGeneration;
import dto.ProgramType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Timer;

import static configuration.ClientConfiguration.REFRESH_RATE;

public class LeftSideController {

    private RightSideController rightController;
    private TopComponentController topController;

    @FXML
    private Button unselectUserBtn;

    @FXML
    private TableView<HistoryTableEntry> userExecutionsTable;

    @FXML
    private TableColumn<HistoryTableEntry, Integer> runIdColumn;

    @FXML
    private TableColumn<HistoryTableEntry, ProgramType> programTypeColumn;

    @FXML
    private TableColumn<HistoryTableEntry, String> programNameColumn;

    @FXML
    private TableColumn<HistoryTableEntry, ArchitectureGeneration> architectureColumn;

    @FXML
    private TableColumn<HistoryTableEntry, Integer> runLevelColumn;

    @FXML
    private TableColumn<HistoryTableEntry, Integer> yValueColumn;

    @FXML
    private TableColumn<HistoryTableEntry, Integer> cyclesConsumedColumn;

    @FXML
    private Button RerunBtn;

    @FXML
    private Button ShowStatisticsBtn;

    @FXML
    private TableView<UserTableEntry> usersTable;

    @FXML
    private TableColumn<UserTableEntry, String> usernameColumn;

    @FXML
    private TableColumn<UserTableEntry, Integer> creditsColumn;

    @FXML
    private TableColumn<UserTableEntry, Integer> creditsUsedColumn;

    @FXML
    private TableColumn<UserTableEntry, Integer> functionsLoadedColumn;

    @FXML
    private TableColumn<UserTableEntry, Integer> programExecutionsCounterColumn;

    @FXML
    private TableColumn<UserTableEntry, Integer> programsLoadedColumn;

    @FXML
    public void initialize() {
        /* Buttons*/
        ObservableValue<UserTableEntry> selectedItemObs = Bindings.select(usersTable.selectionModelProperty(), "selectedItem");
        unselectUserBtn.disableProperty().bind(Bindings.createBooleanBinding(
                () -> {
                    ObservableList<UserTableEntry> users = usersTable.getItems();
                    if (users == null || users.isEmpty()) return true;
                    return selectedItemObs.getValue() == null;
                },
                usersTable.itemsProperty(),
                selectedItemObs
        ));
        RerunBtn.disableProperty().bind(Bindings.isEmpty(userExecutionsTable.getItems()));
        ShowStatisticsBtn.disableProperty().bind(Bindings.isEmpty(userExecutionsTable.getItems()));

        /* Tables */
        usersTable.setRowFactory(tv -> new TableRow<>());

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        programsLoadedColumn.setCellValueFactory(new PropertyValueFactory<>("programsLoaded"));
        functionsLoadedColumn.setCellValueFactory(new PropertyValueFactory<>("functionsLoaded"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        creditsUsedColumn.setCellValueFactory(new PropertyValueFactory<>("creditsUsed"));
        programExecutionsCounterColumn.setCellValueFactory(new PropertyValueFactory<>("programExecutionsCounter"));

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, newUser) -> {
            Platform.runLater(() -> userExecutionsTable.getItems().clear());
        });

        userExecutionsTable.setRowFactory(tv -> new TableRow<>());

        runIdColumn.setCellValueFactory(new PropertyValueFactory<>("run"));
        programTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        programNameColumn.setCellValueFactory(new PropertyValueFactory<>("programName"));
        architectureColumn.setCellValueFactory(new PropertyValueFactory<>("architectureType"));
        runLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        yValueColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        cyclesConsumedColumn.setCellValueFactory(new PropertyValueFactory<>("cycles"));

        userExecutionsTable.placeholderProperty().bind(
                Bindings.createObjectBinding(() -> {
                    if (usersTable.getSelectionModel().getSelectedItem() == null) {
                        return new Label("Current user has no history data");
                    } else {
                        return new Label("This user has no history");
                    }
                }, usersTable.getSelectionModel().selectedItemProperty())
        );



        /* Timer tasks */
        UserListRefresher userListRefreshTask = new UserListRefresher(usersTable);
        Timer timer = new Timer(true);
        timer.schedule(userListRefreshTask, REFRESH_RATE, REFRESH_RATE);
    }

    @FXML
    void unselectUser(ActionEvent event) {
        Platform.runLater(() -> {
            usersTable.getSelectionModel().clearSelection();
            userExecutionsTable.getItems().clear();
        });
    }

    @FXML
    void RerunPressed(ActionEvent event) {

    }

    @FXML
    void ShowStatisticsPressed(ActionEvent event) {

    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;

        /* Timer tasks */
        HistoryTableRefresher userHistoryTableRefresher = new HistoryTableRefresher(userExecutionsTable, topController.getUsername(), usersTable);
        Timer timer = new Timer(true);
        timer.schedule(userHistoryTableRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;
    }
}