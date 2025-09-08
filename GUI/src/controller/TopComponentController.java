package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.application.Platform;

import java.io.File;
import jakarta.xml.bind.JAXBException;
import javafx.stage.StageStyle;

import java.io.FileNotFoundException;

public class TopComponentController{

    private Stage primaryStage;
    private RightSideController rightController;
    private LeftSideController leftController;
    private Controller controller = new SingleProgramController();
    private StringProperty absolutePathProperty;

    private Stage createLoadingDialog(Stage owner, Task<?> task) {
        Label msg = new Label();
        msg.textProperty().bind(task.messageProperty());

        ProgressBar bar = new ProgressBar();
        bar.setPrefWidth(240);
        bar.progressProperty().bind(task.progressProperty());

        VBox root = new VBox(12, msg, bar);
        root.setPadding(new Insets(14));
        root.setFillWidth(true);

        Stage dlg = new Stage(StageStyle.UTILITY);
        dlg.initOwner(owner);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setResizable(false);
        dlg.setTitle("Loading");
        dlg.setScene(new Scene(root));
        return dlg;
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

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    private TextField currentLoadedProgramPath;

    @FXML
    private Button loadFileBtn;

    @FXML
    private void initialize() {
        absolutePathProperty = new SimpleStringProperty("---");
        currentLoadedProgramPath.textProperty().bind(absolutePathProperty);
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
                    controller.loadProgram(absolutePath);
                    // Simulate progress for UI feedback
                    for (int i = 1; i <= 40; i++) {
                        Thread.sleep(50);
                        updateProgress(i, 40);
                    }

                    updateMessage("Done.");
                    return controller.isProgramLoaded();
                } catch (JAXBException | FileNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                    updateMessage("Error: " + e.getMessage());
                    return false;
                }
            }
        };
        // Show modal loading dialog
        Stage dlg = createLoadingDialog(primaryStage, loadTask);
        loadTask.setOnSucceeded(e -> {
            dlg.close();
            if (Boolean.TRUE.equals(loadTask.getValue())) {
                absolutePathProperty.set(absolutePath);
            }
        });
        loadTask.setOnFailed(e -> dlg.close());
        loadTask.setOnCancelled(e -> dlg.close());

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();

        dlg.showAndWait();
    }
}