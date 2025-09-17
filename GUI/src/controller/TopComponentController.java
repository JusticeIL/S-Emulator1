package controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopComponentController{

    private Stage primaryStage;
    private RightSideController rightController;
    private LeftSideController leftController;
    private Model model;
    private StringProperty absolutePathProperty;
    private List<String> availableCSSFileNames;

    @FXML
    private Label currentLoadedProgramPath;

    @FXML
    private Button loadFileBtn;

    @FXML
    private MenuButton skinMenu;

    @FXML
    public void initialize() {
        absolutePathProperty = new SimpleStringProperty("---");
        currentLoadedProgramPath.textProperty().bind(absolutePathProperty);
        availableCSSFileNames = listCssFiles();
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
        dialog.getScene().getStylesheets().add(owner.getScene().getStylesheets().getFirst());
        return dialog;
    }

    public void setModel(SingleProgramController model) {
        this.model = model;
    }

    public void setRightController(RightSideController rightController) {
        this.rightController = rightController;

        // Initialize the skin chooser menu
        skinMenu.getItems().clear();
        skinMenu.disableProperty().bind(
                Bindings.isEmpty(skinMenu.getItems())
        );
        availableCSSFileNames.stream().forEach(fileName -> {
            MenuItem menuItem = new MenuItem(fileName);
            menuItem.setOnAction(event -> {
                primaryStage.getScene().getStylesheets().clear();
                primaryStage.getScene().getStylesheets().add(getClass().getResource("../resources/css/" + fileName + ".css").toExternalForm());
            });
            skinMenu.getItems().add(menuItem);
        });

        // Bind the text property based on whether items are empty or not
        skinMenu.textProperty().bind(
                Bindings.when(Bindings.isEmpty(skinMenu.getItems()))
                        .then("No Available Skin")
                        .otherwise("Choose a Skin")
        );
    }

    public void setLeftController(LeftSideController leftController) {
        this.leftController = leftController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        if (rightController != null) {
            rightController.setPrimaryStage(primaryStage);
        }
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
                        leftController.clearHistoryChainTable(); // Clear history chain table on new loaded program

                    }
                    if (rightController != null) {
                        rightController.updateArgumentTable();
                        rightController.clearStatisticsTable();
                        rightController.updateIsDebugProperty();
                        rightController.OnProgramLoaded();
                        rightController.clearVariableTable(); // Clear variable table on new loaded program

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
                Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK); // Helps access and style the ok button
                okButton.setId("ok-button");
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

    public List<String> listCssFiles() {
        List<String> cssFiles = new ArrayList<>();
        try {
            URL folderUrl = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("resources/css");
            if (folderUrl != null) {
                Path folderPath = Paths.get(folderUrl.toURI());
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath, "*.css")) {
                    for (Path entry : stream) {
                        cssFiles.add(removeExtension(entry.getFileName().toString()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(cssFiles);
        return cssFiles.reversed();
    }

    public static String removeExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return fileName; // Case: no extension found
        }
        return fileName.substring(0, lastDot);
    }
}