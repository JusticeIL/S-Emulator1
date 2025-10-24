package dashboard.controller;

import com.google.gson.Gson;
import dashboard.model.HistoryTableEntry;
import dashboard.model.UserTableEntry;
import dashboard.refreshTasks.HistoryTableRefresher;
import dashboard.refreshTasks.UserListRefresher;
import dto.ArchitectureGeneration;
import dto.ProgramType;
import dto.VariableDTO;
import execution.controller.PrimaryController;
import execution.model.ArgumentTableEntry;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static configuration.ClientConfiguration.CLIENT;
import static configuration.ClientConfiguration.REFRESH_RATE;
import static configuration.DialogUtils.showAlert;
import static configuration.ResourcesConfiguration.*;

public class LeftSideController {

    private RightSideController rightController;
    private TopComponentController topController;
    private boolean isShowHistoryDialogOpen = false;

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
        Stage primaryStage = (Stage) userExecutionsTable.getScene().getWindow();

        if (userExecutionsTable.getSelectionModel().getSelectedItem() == null) {
            showAlert("No run has been selected", primaryStage);
            return;
        } else {
            HistoryTableEntry selectedItem = userExecutionsTable.getSelectionModel().getSelectedItem();
            String programName = selectedItem.getProgramName();

            Gson gson = new Gson();
            String json = gson.toJson(Map.of("programName", programName));

            RequestBody body = RequestBody.create(
                    json,
                    MediaType.parse("application/json")
            );

            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + SET_ACTIVE_PROGRAM_RESOURCE))
                    .newBuilder();
            String finalURL = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(finalURL)
                    .put(body)
                    .build();

            Call call = CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            try {
                                Stage primaryStage = (Stage) userExecutionsTable.getScene().getWindow();

                                if (!primaryStage.isFocused()) {
                                    return;
                                }

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/execution/resources/fxml/execution.fxml"));
                                Parent newRoot = loader.load();
                                // Create new scene
                                Scene executionScene = new Scene(newRoot, 850, 600);

                                // Copy current scene stylesheets (preserves chosen skin)
                                Scene currentScene = userExecutionsTable.getScene();

                                executionScene.getStylesheets().setAll(currentScene.getStylesheets());

                                // Fallback: ensure at least a default stylesheet if none copied
                                if (executionScene.getStylesheets().isEmpty()) { // Case: no stylesheet was copied because empty
                                    executionScene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
                                }

                                PrimaryController controller = loader.getController();
                                // Close current window
                                ((Stage) userExecutionsTable.getScene().getWindow()).close();

                                new Thread(() -> {
                                    controller.getTopComponentController().setPrimaryStage(primaryStage);
                                    controller.getLeftSideController().sendExpansionForActiveProgramRequest(selectedItem.getLevel(), selectedItem);
                                }).start();
                                primaryStage.setScene(executionScene);
                                primaryStage.setTitle("S-embler - Execution");
                                primaryStage.getIcons().add(
                                        new Image(getClass().getResourceAsStream("/resources/icon.png"))
                                );
                                primaryStage.show();
                            } catch (Exception ex) {
                                Stage primaryStage = (Stage) userExecutionsTable.getScene().getWindow();
                                showAlert("Failed to load execution: " + ex.getMessage(), primaryStage);
                            }
                        });
                    } else {
                        try (ResponseBody body = response.body()) {
                            String responseBody = Objects.requireNonNull(body).string();
                            showAlert("Failed to set an active program: " + response.code() + "\n" + responseBody, primaryStage);
                        } catch (Exception e) {
                            showAlert("Failed to set an active program: " + response.code(), primaryStage);
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    void ShowStatisticsPressed(ActionEvent event) {
        if (!isShowHistoryDialogOpen && !userExecutionsTable.getSelectionModel().isEmpty()) {
            isShowHistoryDialogOpen = true;
            HistoryTableEntry entry = userExecutionsTable.getSelectionModel().getSelectedItem();
            Map<String, Integer> allEntryVariables = entry.getAllVariables();
            Stage dialogStage = createVariablesTableDialog(allEntryVariables);
            dialogStage.setOnCloseRequest(closeEvent -> isShowHistoryDialogOpen = false);
            dialogStage.show();
        }
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

    private Stage createVariablesTableDialog(Map<String, Integer> allEntryVariables) {
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
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(true);
        tableView.setPrefSize(200, 200);
        tableView.setPadding(new Insets(20, 20, 50, 20));
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
        scene.getStylesheets().add(userExecutionsTable.getScene().getStylesheets().getFirst());

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
}