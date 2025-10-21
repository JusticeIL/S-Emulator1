package login.controller;

import dashboard.controller.PrimaryController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import okhttp3.*;

import java.util.Objects;

import static configuration.ClientConfiguration.CLIENT;
import static configuration.DialogUtils.showAlert;
import static configuration.ResourcesConfiguration.BASE_URL;
import static configuration.ResourcesConfiguration.USER_RESOURCE;

public class MainController {

    @FXML
    private Label clientApplicationTitle;

    @FXML
    private Button registerUserBtn;

    @FXML
    private TextField usernameField;

    @FXML
    public void initialize() {
        // Allow pressing Enter to trigger registration
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                registerUserBtn.fire(); // Triggers the ActionEvent
            }
        });
    }

    @FXML
    void tryRegisterUserBtn(ActionEvent event) {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + USER_RESOURCE))
                    .newBuilder()
                    .addQueryParameter("username", usernameField.getText());
            String finalURL = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(finalURL)
                    .post(RequestBody.create(new byte[]{}))
                    .build();

            Call call = CLIENT.newCall(request);

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

                                PrimaryController controller = loader.getController();
                                Stage primaryStage = (Stage) clientApplicationTitle.getScene().getWindow();

                                // Close current window
                                ((Stage) registerUserBtn.getScene().getWindow()).close();

                                controller.getTopComponentController().setPrimaryStage(primaryStage);
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
}