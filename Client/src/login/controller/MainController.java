package login.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;

import java.util.Objects;

public class MainController {

    private OkHttpClient client;
    private final String BASE_URL = "http://localhost:8080/S-emulator";
    private final String RESOURCE = "/api/user/register";

    @FXML
    private Label clientApplicationTitle;

    @FXML
    private Button registerUserBtn;

    @FXML
    private TextField usernameField;

    @FXML
    public void initialize() {
        client = new OkHttpClient();
    }

    @FXML
    void tryRegisterUserBtn(ActionEvent event) {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE))
                    .newBuilder()
                    .addQueryParameter("username", usernameField.getText());
            String finalURL = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(finalURL)
                    .post(RequestBody.create(new byte[]{}))
                    .build();

            Call call = client.newCall(request);

            new Thread(() -> {
                try (Response response = call.execute()) {
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/resources/fxml/dashboard.fxml"));
                                Parent newRoot = loader.load();
                                Scene dashboardScene = new Scene(newRoot, 850, 600);
                                dashboardScene.getStylesheets().clear();
                                dashboardScene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());

                                Stage primaryStage = (Stage) clientApplicationTitle.getScene().getWindow();
                                // Close current window
                                ((Stage) registerUserBtn.getScene().getWindow()).close();

                                primaryStage.setScene(dashboardScene);
                                primaryStage.setTitle("S-embler - Dashboard");
                                primaryStage.getIcons().add(
                                        new Image(getClass().getResourceAsStream("/resources/icon.png"))
                                );
                                primaryStage.show();
                            } catch (Exception ex) {
                                Stage primaryStage = (Stage) clientApplicationTitle.getScene().getWindow();
                                showAlert("Failed to load dashboard: " + ex.getMessage(), primaryStage);
                            }
                        });
                    } else {
                        Stage primaryStage = (Stage) clientApplicationTitle.getScene().getWindow();
                        String errorMsg = response.body() != null ? response.body().string() : "Unknown error";
                        showAlert(errorMsg, primaryStage);
                    }
                } catch (Exception e) {
                    Stage primaryStage = (Stage) clientApplicationTitle.getScene().getWindow();
                    showAlert("Error: " + e.getMessage(), primaryStage);
                }
            }).start();
    }

    //TODO: Refactor this method to a utility class if needed in other controllers
    public static void showAlert(String message, Stage primaryStage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK); // Helps access and style the ok button
            okButton.getStyleClass().add("ok-button");
            alert.initOwner(primaryStage);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle("Error");
            alert.setHeaderText("Error occurred");
            alert.setContentText(message);

            // Ensure the dialog is focused
            alert.setOnShown(dialogEvent -> alert.getDialogPane().requestFocus());
            alert.showAndWait();
        });
    }

    public OkHttpClient getClient() {
        return client;
    }
}