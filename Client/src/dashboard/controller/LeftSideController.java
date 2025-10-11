package dashboard.controller;

import dashboard.model.UserTableEntry;
import dashboard.refreshTasks.UserListRefresher;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.OkHttpClient;

import java.util.Timer;

public class LeftSideController {

    private RightSideController rightController;
    private TopComponentController topController;
    private OkHttpClient client;
    private final int REFRESH_RATE = 1500; // in milliseconds

    @FXML
    private Button unselectUserBtn;

    @FXML
    private TableView<?> userExecutionsTable;

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
        unselectUserBtn.disableProperty().bind(Bindings.isEmpty(usersTable.getItems()));
        RerunBtn.disableProperty().bind(Bindings.isEmpty(userExecutionsTable.getItems()));
        ShowStatisticsBtn.disableProperty().bind(Bindings.isEmpty(userExecutionsTable.getItems()));

        /* Tables */
        usersTable.setRowFactory(tv -> new TableRow<UserTableEntry>());

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        programsLoadedColumn.setCellValueFactory(new PropertyValueFactory<>("programsLoaded"));
        functionsLoadedColumn.setCellValueFactory(new PropertyValueFactory<>("functionsLoaded"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        creditsUsedColumn.setCellValueFactory(new PropertyValueFactory<>("creditsUsed"));
        programExecutionsCounterColumn.setCellValueFactory(new PropertyValueFactory<>("programExecutionsCounter"));
    }

    @FXML
    void unselectUser(ActionEvent event) {

    }

    @FXML
    void RerunPressed(ActionEvent event) {

    }

    @FXML
    void ShowStatisticsPressed(ActionEvent event) {

    }

    public void setClient(OkHttpClient client) {
        this.client = client;

        UserListRefresher userListRefreshTask = new UserListRefresher(client, usersTable);
        Timer timer = new Timer();
        timer.schedule(userListRefreshTask, REFRESH_RATE, REFRESH_RATE);
    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;
    }
}