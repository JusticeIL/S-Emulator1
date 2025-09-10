import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class SemblerGUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resources/fxml/primary.fxml"));
        try {
            primaryStage.setTitle("S-embler");
            primaryStage.setScene(new javafx.scene.Scene(fxmlLoader.load(), 800, 600));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception { // TODO: implement?
        ;
    }

    public static void main(String[] args) {
        launch();
    }
}