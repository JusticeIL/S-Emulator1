package dashboard.controller;

import dashboard.model.UserTableEntry;
import dashboard.refreshTasks.UserListRefresher;
import javafx.beans.binding.Bindings;
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

        /* Timer tasks */
        UserListRefresher userListRefreshTask = new UserListRefresher(usersTable);
        Timer timer = new Timer();
        timer.schedule(userListRefreshTask, REFRESH_RATE, REFRESH_RATE);
    }

    @FXML
    void unselectUser(ActionEvent event) {
        usersTable.getSelectionModel().clearSelection();
    }

    @FXML
    void RerunPressed(ActionEvent event) {

    }

    @FXML
    void ShowStatisticsPressed(ActionEvent event) {

    }

    public void setTopController(TopComponentController topController) {
        this.topController = topController;
    }

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;
    }
}