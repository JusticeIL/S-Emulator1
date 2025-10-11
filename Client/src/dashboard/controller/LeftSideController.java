package dashboard.controller;

import dashboard.refreshTasks.UserListRefresher;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private TableView<?> usersTable;

    @FXML
    public void initialize() {
        unselectUserBtn.disableProperty().bind(Bindings.isEmpty(usersTable.getItems()));
        RerunBtn.disableProperty().bind(Bindings.isEmpty(userExecutionsTable.getItems()));
        ShowStatisticsBtn.disableProperty().bind(Bindings.isEmpty(userExecutionsTable.getItems()));

        UserListRefresher userListRefreshTask = new UserListRefresher(client);
        Timer timer = new Timer();
        timer.schedule(userListRefreshTask, REFRESH_RATE, REFRESH_RATE);
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
    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;
    }
}