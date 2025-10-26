package dashboard.controller;

import com.google.gson.Gson;
import dashboard.model.FunctionTableEntry;
import dashboard.model.ProgramTableEntry;
import dashboard.refreshTasks.FunctionsTableRefresher;
import dashboard.refreshTasks.ProgramsTableRefresher;
import execution.controller.PrimaryController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;

import static configuration.ClientConfiguration.CLIENT;
import static configuration.ClientConfiguration.REFRESH_RATE;
import static configuration.DialogUtils.showAlert;
import static configuration.ResourcesConfiguration.*;

public class RightSideController {

    @FXML
    private TableView<ProgramTableEntry> programsTable;

    @FXML
    private TableColumn<ProgramTableEntry, String> programNameColumn;

    @FXML
    private TableColumn<ProgramTableEntry, String> userProgramOriginColumn;

    @FXML
    private TableColumn<ProgramTableEntry, Integer> instructionsCounterProgramColumn;

    @FXML
    private TableColumn<ProgramTableEntry, Integer> programMaxLevelColumn;

    @FXML
    private TableColumn<ProgramTableEntry, Integer> executionsCounterColumn;

    @FXML
    private TableColumn<ProgramTableEntry, Number> averageCostColumn;

    @FXML
    private Button executeProgramBtn;

    @FXML
    private TableView<FunctionTableEntry> functionsTable;

    @FXML
    private TableColumn<FunctionTableEntry, String> functionNameColumn;

    @FXML
    private TableColumn<FunctionTableEntry, String> programOriginColumn;

    @FXML
    private TableColumn<FunctionTableEntry, String> userFunctionOriginColumn;

    @FXML
    private TableColumn<FunctionTableEntry, Integer> instructionsCounterFunctionColumn;

    @FXML
    private TableColumn<FunctionTableEntry, Integer> functionMaxLevelColumn;

    @FXML
    private Button executeFunctionBtn;

    @FXML
    public void initialize() {
        /* Buttons */
        ObservableValue<ProgramTableEntry> selectedItemProgramObs = Bindings.select(programsTable.selectionModelProperty(), "selectedItem");
        executeProgramBtn.disableProperty().bind(Bindings.createBooleanBinding(
                () -> {
                    ObservableList<ProgramTableEntry> programs = programsTable.getItems();
                    if (programs == null || programs.isEmpty()) return true;
                    return selectedItemProgramObs.getValue() == null;
                },
                programsTable.itemsProperty(),
                selectedItemProgramObs
        ));
        ObservableValue<FunctionTableEntry> selectedItemFunctionObs = Bindings.select(functionsTable.selectionModelProperty(), "selectedItem");
        executeFunctionBtn.disableProperty().bind(Bindings.createBooleanBinding(
                () -> {
                    ObservableList<FunctionTableEntry> functions = functionsTable.getItems();
                    if (functions == null || functions.isEmpty()) return true;
                    return selectedItemFunctionObs.getValue() == null;
                },
                functionsTable.itemsProperty(),
                selectedItemFunctionObs
        ));

        /* Tables */
        programsTable.setRowFactory(tv -> new TableRow<>());

        programNameColumn.setCellValueFactory(new PropertyValueFactory<>("programName"));
        userProgramOriginColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        instructionsCounterProgramColumn.setCellValueFactory(new PropertyValueFactory<>("instructionsCounter"));
        programMaxLevelColumn.setCellValueFactory(new PropertyValueFactory<>("maxProgramLevel"));
        executionsCounterColumn.setCellValueFactory(new PropertyValueFactory<>("executionsCounter"));
        averageCostColumn.setCellValueFactory(new PropertyValueFactory<>("averageExecutionCost"));

        functionsTable.setRowFactory(tv -> new TableRow<>());

        functionNameColumn.setCellValueFactory(new PropertyValueFactory<>("functionName"));
        programOriginColumn.setCellValueFactory(new PropertyValueFactory<>("programOrigin"));
        userFunctionOriginColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        instructionsCounterFunctionColumn.setCellValueFactory(new PropertyValueFactory<>("instructionsCounter"));
        functionMaxLevelColumn.setCellValueFactory(new PropertyValueFactory<>("maxProgramLevel"));

        /* Timer tasks */
        ProgramsTableRefresher programListRefreshTask = new ProgramsTableRefresher(programsTable);
        Timer timer1 = new Timer(true);
        timer1.schedule(programListRefreshTask, REFRESH_RATE, REFRESH_RATE);

        FunctionsTableRefresher functionListRefreshTask = new FunctionsTableRefresher(functionsTable);
        Timer timer2 = new Timer(true);
        timer2.schedule(functionListRefreshTask, REFRESH_RATE, REFRESH_RATE);
    }

    @FXML
    void executeFunction(ActionEvent event) {

        Stage primaryStage = (Stage) functionsTable.getScene().getWindow();

        if (functionsTable.getSelectionModel().getSelectedItem() == null) {
            showAlert("No program selected", primaryStage);
            return;
        } else {
            FunctionTableEntry selectedItem = functionsTable.getSelectionModel().getSelectedItem();
            String programName = selectedItem.getFunctionName();

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

                    try (response) {
                        if (response.isSuccessful()) {
                            Platform.runLater(() -> {
                                try {
                                    Stage primaryStage = (Stage) functionsTable.getScene().getWindow();

                                    if (!primaryStage.isFocused()) {
                                        return;
                                    }

                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/execution/resources/fxml/execution.fxml"));
                                    Parent newRoot = loader.load();
                                    // Create new scene
                                    Scene executionScene = new Scene(newRoot, 850, 600);

                                    // Copy current scene stylesheets (preserves chosen skin)
                                    Scene currentScene = functionsTable.getScene();

                                    executionScene.getStylesheets().setAll(currentScene.getStylesheets());

                                    // Fallback: ensure at least a default stylesheet if none copied
                                    if (executionScene.getStylesheets().isEmpty()) { // Case: no stylesheet was copied because empty
                                        executionScene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
                                    }

                                    PrimaryController controller = loader.getController();

                                    // Close current window
                                    ((Stage) functionsTable.getScene().getWindow()).close();

                                    controller.getTopComponentController().setPrimaryStage(primaryStage);
                                    primaryStage.setScene(executionScene);
                                    primaryStage.setTitle("S-embler - Execution");
                                    primaryStage.getIcons().add(
                                            new Image(getClass().getResourceAsStream("/resources/icon.png"))
                                    );
                                    controller.getRightSideController().clearVariablesTable();
                                    primaryStage.show();
                                } catch (Exception ex) {
                                    Stage primaryStage = (Stage) functionsTable.getScene().getWindow();
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
                    } catch (Exception e) {
                        showAlert("Failed to close the connection properly", primaryStage);
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    showAlert("Failed to get the response from the server, " + e.getMessage(), primaryStage);
                }
            });
        }
    }

    @FXML
    void executeProgram(ActionEvent event) {

        Stage primaryStage = (Stage) programsTable.getScene().getWindow();

        if (programsTable.getSelectionModel().getSelectedItem() == null) {
            showAlert("No program selected", primaryStage);
            return;
        } else {
            ProgramTableEntry selectedItem = programsTable.getSelectionModel().getSelectedItem();
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

                    try (response) {
                        if (response.isSuccessful()) {
                            Platform.runLater(() -> {
                                try {
                                    Stage primaryStage = (Stage) programsTable.getScene().getWindow();

                                    if (!primaryStage.isFocused()) {
                                        return;
                                    }

                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/execution/resources/fxml/execution.fxml"));
                                    Parent newRoot = loader.load();
                                    // Create new scene
                                    Scene executionScene = new Scene(newRoot, 850, 600);

                                    // Copy current scene stylesheets (preserves chosen skin)
                                    Scene currentScene = programsTable.getScene();

                                    executionScene.getStylesheets().setAll(currentScene.getStylesheets());

                                    // Fallback: ensure at least a default stylesheet if none copied
                                    if (executionScene.getStylesheets().isEmpty()) { // Case: no stylesheet was copied because empty
                                        executionScene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
                                    }

                                    PrimaryController controller = loader.getController();

                                    // Close current window
                                    ((Stage) programsTable.getScene().getWindow()).close();

                                    controller.getTopComponentController().setPrimaryStage(primaryStage);
                                    primaryStage.setScene(executionScene);
                                    primaryStage.setTitle("S-embler - Execution");
                                    primaryStage.getIcons().add(
                                            new Image(getClass().getResourceAsStream("/resources/icon.png"))
                                    );
                                    controller.getRightSideController().clearVariablesTable();
                                    primaryStage.show();
                                } catch (Exception ex) {
                                    Stage primaryStage = (Stage) programsTable.getScene().getWindow();
                                    showAlert("Failed to load execute: " + ex.getMessage(), primaryStage);
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
                    } catch (Exception e) {
                        showAlert("Failed to close the connection properly", primaryStage);
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    showAlert("Failed to get the response from the server, " + e.getMessage(), primaryStage);
                }
            });
        }
    }
}