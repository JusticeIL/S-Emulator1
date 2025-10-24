package execution.controller;

import com.google.gson.Gson;
import dto.UserDTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static configuration.ClientConfiguration.CLIENT;
import static configuration.DialogUtils.showAlert;
import static configuration.ResourcesConfiguration.BASE_URL;
import static configuration.ResourcesConfiguration.USER_RESOURCE;

public class TopComponentController{

    private Stage primaryStage;
    private PrimaryController primaryController;
    private RightSideController rightController;
    private LeftSideController leftController;
    private List<String> availableCSSFileNames;

    @FXML
    private Label userNameDisplay;

    @FXML
    private Label currentProgramName;

    @FXML
    private Label currentCredits;

    @FXML
    private CheckBox allowAnimationBox;

    @FXML
    private MenuButton skinMenu;

    @FXML
    public void initialize() {
        availableCSSFileNames = listCssFiles();

        HttpUrl url = HttpUrl.parse(BASE_URL);
        List<Cookie> cookies = CLIENT.cookieJar().loadForRequest(Objects.requireNonNull(url));

        for (Cookie cookie : cookies) {
            if ("username".equals(cookie.name())) {
                userNameDisplay.setText(cookie.value());
            }
        }

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + USER_RESOURCE))
                .newBuilder()
                .addQueryParameter("username", userNameDisplay.getText());
        String finalURL = urlBuilder.build().toString();

        // Building the request based on the body from above
        Request request = new Request.Builder()
                .url(finalURL)
                .get()
                .build();

        Call call = CLIENT.newCall(request);

        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                try (ResponseBody responseBody = response.body()) {
                    Gson gson = new Gson();
                    UserDTO user = gson.fromJson(Objects.requireNonNull(responseBody).string(), UserDTO.class);
                    Platform.runLater(() -> {
                        currentCredits.setText("Available Credits: " + user.getCredits());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                try (ResponseBody body = response.body()) {
                    String responseBody = Objects.requireNonNull(body).string();
                    showAlert("Failed to retrieve user data: " + response.code() + "\n" + responseBody, primaryStage);
                } catch (Exception e) {
                    showAlert("Failed to retrieve user data: " + response.code(), primaryStage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                if (scene == null) { // Case: No scene available on primaryStage
                    return;
                }

                // Build candidate resource paths (absolute for Class.getResource)
                String[] candidates = new String[] {
                        "/css/" + fileName + ".css",
                        "/css/" + fileName + ".css"
                };

                URL cssUrl = null;
                for (String p : candidates) {
                    cssUrl = getClass().getResource(p);
                    if (cssUrl != null) break;
                }

                if (cssUrl == null) { // Case: CSS file not found
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

    public void setPrimaryController(PrimaryController primaryController) {
        this.primaryController = primaryController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        if (rightController != null) {
            rightController.setPrimaryStage(primaryStage);
        }
    }

    public List<String> listCssFiles() {
        final String resourcePath = "css";
        List<String> cssFiles = new ArrayList<>();

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL dirURL = cl.getResource(resourcePath);

            if (dirURL != null) {
                String protocol = dirURL.getProtocol();

                if ("file".equals(protocol)) { // Case: IDE / filesystem
                    Path folder = Paths.get(dirURL.toURI());
                    try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder, "*.css")) {
                        for (Path p : ds) cssFiles.add(removeExtension(p.getFileName().toString()));
                    }
                } else if ("jar".equals(protocol)) { // Case: jar entry
                    String fullPath = dirURL.getPath();
                    String jarPath = fullPath.substring(5, fullPath.indexOf("!"));
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
                } else { // Case: unknown protocol
                    Enumeration<URL> urls = cl.getResources(resourcePath);
                    while (urls.hasMoreElements()) {
                        URL u = urls.nextElement();
                    }
                }
            } else { // Case: resourcePath not found; check whether you packaged CSS under a different path.
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Collections.sort(cssFiles);
        Collections.reverse(cssFiles);
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

    public void sendUpdateCreditsRequest() {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + USER_RESOURCE))
                .newBuilder()
                .addQueryParameter("username", userNameDisplay.getText());
        String finalURL = urlBuilder.build().toString();

        // Building the request based on the body from above
        Request request = new Request.Builder()
                .url(finalURL)
                .get()
                .build();

        Call call = CLIENT.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (ResponseBody responseBody = response.body()) {
                        Gson gson = new Gson();
                        UserDTO user = gson.fromJson(Objects.requireNonNull(responseBody).string(), UserDTO.class);
                        Platform.runLater(() -> {
                            currentCredits.setText("Available Credits: " + user.getCredits());
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    try (ResponseBody body = response.body()) {
                        String responseBody = Objects.requireNonNull(body).string();
                        showAlert("Failed to retrieve user data: " + response.code() + "\n" + responseBody, primaryStage);
                    } catch (Exception e) {
                        showAlert("Failed to retrieve user data: " + response.code(), primaryStage);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void initAllFields() {
        if (primaryController.program != null) {
            Platform.runLater(() -> currentProgramName.setText(primaryController.program.getProgramName()));
        }
    }
}