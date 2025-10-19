package configuration;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogUtils {
    public static void showAlert(String message, Stage primaryStage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK); // Helps access and style the ok button
            okButton.getStyleClass().add("ok-button-error");
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
}
