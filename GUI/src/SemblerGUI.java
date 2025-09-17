import controller.PrimaryController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SemblerGUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resources/fxml/primary.fxml"));;
        try {
            Parent root = fxmlLoader.load();
            PrimaryController primaryController = fxmlLoader.getController();
            primaryStage.setTitle("S-embler");
            primaryStage.setScene(new Scene(root, 800, 600));
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