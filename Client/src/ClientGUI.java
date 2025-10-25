import login.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static configuration.ClientConfiguration.CLIENT;

public class ClientGUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login/resources/fxml/login.fxml"));
        try {
            Parent root = fxmlLoader.load();
            MainController mainController = fxmlLoader.getController();
            primaryStage.setTitle("S-embler");
            primaryStage.getIcons().add(
                    new Image(getClass().getResourceAsStream("resources/icon.png"))
            );
            Scene primaryScene = new Scene(root, 360, 480);
            primaryScene.getStylesheets().clear();
            primaryScene.getStylesheets().add(getClass().getResource("login/resources/css/login.css").toExternalForm());
            primaryStage.setScene(primaryScene);

            primaryStage.setOnCloseRequest(event -> {
                new Thread(() -> {
                    CLIENT.dispatcher().executorService().shutdown();
                    CLIENT.connectionPool().evictAll();
                }).start();
            });

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}