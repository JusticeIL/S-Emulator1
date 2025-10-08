import dashboard.controller.PrimaryController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class test extends Application {
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard/resources/fxml/dashboard.fxml"));
        try {
            Parent root = fxmlLoader.load();
            PrimaryController primaryController = fxmlLoader.getController();
            primaryStage.setTitle("S-embler");
            Scene primaryScene = new Scene(root, 850, 600);
            primaryScene.getStylesheets().clear();
            primaryScene.getStylesheets().add(getClass().getResource("css/dark-mode.css").toExternalForm());
            primaryStage.setScene(primaryScene);
            primaryController.getTopComponentController().setPrimaryStage(primaryStage);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
