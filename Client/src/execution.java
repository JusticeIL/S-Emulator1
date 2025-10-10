import execution.controller.PrimaryController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class execution extends Application {
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("execution/resources/fxml/execution.fxml"));
        try {
            Parent root = fxmlLoader.load();
            PrimaryController primaryController = fxmlLoader.getController();
            primaryStage.setTitle("S-embler - Execution");
            primaryStage.getIcons().add(
                    new Image(getClass().getResourceAsStream("resources/icon.png"))
            );
            Scene primaryScene = new Scene(root, 1050, 600);
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