package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.concurrent.Task;

import java.io.File;
import jakarta.xml.bind.JAXBException;
import javafx.stage.StageStyle;

import java.io.FileNotFoundException;

public class TopComponentController{

    private Stage primaryStage;
    private RightSideController rightController;
    private LeftSideController leftController;
    private Model model;
    private StringProperty absolutePathProperty;

    @FXML
    private Label currentLoadedProgramPath;

    @FXML
    private Button loadFileBtn;

    @FXML
    public void initialize() {
        absolutePathProperty = new SimpleStringProperty("---");
        currentLoadedProgramPath.textProperty().bind(absolutePathProperty);

    }

    private Stage createLoadingDialog(Stage owner, Task<?> task) {
        Label msg = new Label();
        msg.textProperty().bind(task.messageProperty());

        ProgressBar bar = new ProgressBar();
        bar.setPrefWidth(240);
        bar.progressProperty().bind(task.progressProperty());

        VBox root = new VBox(12, msg, bar);
        root.setPadding(new Insets(14));
        root.setFillWidth(true);

        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setTitle("Loading");
        dialog.setScene(new Scene(root));
        return dialog;
    }

    public void setModel(SingleProgramController model) {
        this.model = model;
    }

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;
    }

    public void setLeftController(LeftSideController leftController) {
        this.leftController = leftController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    void loadProgramPressed(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Program");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Program File", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();
        Task<Boolean> loadTask = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                updateProgress(0, 1);
                try {
                    model.loadProgram(absolutePath);
                    // Simulate progress for UI feedback
                    for (int i = 1; i <= 40; i++) {
                        Thread.sleep(50);
                        updateProgress(i, 40);
                    }

                    updateMessage("Done.");
                    return model.isProgramLoaded();
                } catch (JAXBException | FileNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                    updateMessage("Error: " + e.getMessage());
                    return false;
                }
            }
        };
        // Show modal loading dialog
        Stage dialog = createLoadingDialog(primaryStage, loadTask);
        loadTask.setOnSucceeded(successEvent -> {
            dialog.close();
            if (Boolean.TRUE.equals(loadTask.getValue())) {
                Platform.runLater(() -> {
                    absolutePathProperty.set(absolutePath);
                    // Update main table after loading succeeded
                    if (leftController != null) {
                        leftController.updateMainInstructionTable();
                        leftController.setCurrentLevel(0);
                        leftController.updateExpansionLevels();
                        leftController.resetLevelExpansionButtonText();
                        leftController.updateVariablesOrLabelSelectionMenu();

                    }
                    if (rightController != null) {
                        rightController.updateArgumentTable();
                        rightController.clearStatisticsTable();
                        rightController.OnProgramLoaded();
                    }
                });
            }
        });
        loadTask.setOnFailed(failEvent -> {
            try { dialog.close(); } catch (Exception ignored) {}

            // Extract meaningful message from the thrown exception
            Throwable exception = loadTask.getException();
            String message = (exception == null) ? "Unknown error" : (exception.getMessage() != null ? exception.getMessage() : exception.toString());

            // Show the error dialog on the FX thread
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(primaryStage);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setTitle("Error");
                alert.setHeaderText("Program Load Failed");
                alert.setContentText(message);

                // Ensure the dialog is focused
                alert.setOnShown(dialogEvent -> alert.getDialogPane().requestFocus());

                alert.showAndWait();
            });
        });
        loadTask.setOnCancelled(e -> dialog.close());

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();

        dialog.showAndWait();
    }
}