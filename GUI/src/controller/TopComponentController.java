package controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
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
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
    private CheckBox allowAnimationBox;

    @FXML
    private MenuButton skinMenu;

    @FXML
    public void initialize() {
        absolutePathProperty = new SimpleStringProperty("---");
        currentLoadedProgramPath.textProperty().bind(absolutePathProperty);
        availableCSSFileNames = listCssFiles();
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
                        leftController.updateMaxExpansionLevel();
                        leftController.updateVariablesOrLabelSelectionMenu();
                        leftController.clearHistoryChainTable(); // Clear history chain table on new loaded program
                        leftController.updateFunctionOrProgramSelectionMenu();
                        leftController.setProgramNameInChooser();
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
                Scene scene = primaryStage.getScene();
                if (scene == null) {
                    System.err.println("No scene available on primaryStage");
                    return;
                }

                // Build candidate resource paths (absolute for Class.getResource)
                String[] candidates = new String[] {
                        "/resources/css/" + fileName + ".css",
                        "/css/" + fileName + ".css"
                };

                URL cssUrl = null;
                for (String p : candidates) {
                    cssUrl = getClass().getResource(p);
                    if (cssUrl != null) break;
                }

                if (cssUrl == null) {
                    System.err.println("CSS resource not found for fileName: " + fileName +
                            ". Tried: " + Arrays.toString(candidates));
                    // optional: log listCssFiles() if you have that helper
                    return;
                }

                scene.getStylesheets().clear();
                scene.getStylesheets().add(cssUrl.toExternalForm());
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

    public List<String> listCssFiles() {
        final String resourcePath = "resources/css"; // adjust if your CSS ends up at a different path in the JAR
        List<String> cssFiles = new ArrayList<>();

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL dirURL = cl.getResource(resourcePath);

            if (dirURL != null) {
                String protocol = dirURL.getProtocol();

                if ("file".equals(protocol)) {
                    // running in IDE / exploded classes on filesystem
                    Path folder = Paths.get(dirURL.toURI());
                    try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder, "*.css")) {
                        for (Path p : ds) cssFiles.add(removeExtension(p.getFileName().toString()));
                    }
                } else if ("jar".equals(protocol)) {
                    // running from JAR: iterate jar entries
                    // dirURL example: jar:file:/C:/.../your.jar!/resources/css
                    String fullPath = dirURL.getPath(); // "file:/C:/.../your.jar!/resources/css"
                    String jarPath = fullPath.substring(5, fullPath.indexOf("!")); // remove "file:" and after '!'
                    jarPath = URLDecoder.decode(jarPath, "UTF-8");

                    try (JarFile jar = new JarFile(jarPath)) {
                        Enumeration<JarEntry> entries = jar.entries();
                        String prefix = resourcePath + "/";
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.startsWith(prefix) && name.endsWith(".css") && !entry.isDirectory()) {
                                String fileName = name.substring(prefix.length());
                                cssFiles.add(removeExtension(fileName));
                            }
                        }
                    }
                } else {
                    // some other protocol (rare)
                    // attempt a fallback by listing resources via getResources
                    Enumeration<URL> urls = cl.getResources(resourcePath);
                    while (urls.hasMoreElements()) {
                        URL u = urls.nextElement();
                        // you could repeat logic above for each u
                    }
                }
            } else {
                // resourcePath not found; check whether you packaged CSS under a different path.
                System.err.println("Resource folder not found on classpath: " + resourcePath);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Collections.sort(cssFiles);
        Collections.reverse(cssFiles); // you had reversed() in your original code — do this if required
        return cssFiles;
    }

    // helper:
    private String removeExtension(String s) {
        int i = s.lastIndexOf('.');
        return (i == -1) ? s : s.substring(0, i);
    }


    public BooleanProperty isAnimationAllowedProperty() {
        return allowAnimationBox.selectedProperty();
    }
}