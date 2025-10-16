package dashboard.controller;

import com.google.gson.Gson;
import dto.UserDTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
import static configuration.ResourcesConfiguration.*;

public class TopComponentController{

    private Stage primaryStage;
    private RightSideController rightController;
    private LeftSideController leftController;
    private List<String> availableCSSFileNames;

    @FXML
    private Label userNameDisplay;

    @FXML
    private Label currentCredits;

    @FXML
    private MenuButton skinMenu;

    @FXML
    private Button loadFileBtn;

    @FXML
    private Label currentLoadedProgramPath;

    @FXML
    private Button addCreditsBtn;

    @FXML
    private TextField creditsTextField;

    @FXML
    public void initialize() {
        HttpUrl url = HttpUrl.parse(BASE_URL);
        List<Cookie> cookies = CLIENT.cookieJar().loadForRequest(Objects.requireNonNull(url));

        for (Cookie cookie : cookies) {
            if ("username".equals(cookie.name())) {
                userNameDisplay.setText(cookie.value());
            }
        }

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + USER_RESOURCE))
                .newBuilder();
        String finalURL = urlBuilder.build().toString();

        // Building the request based on the body from above
        Request request = new Request.Builder()
                .url(finalURL)
                .addHeader("Cookie", "username=" + userNameDisplay.getText())
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

            } else { //TODO: handle error by creating a new dialog window
                System.out.println("Failed to send program to server: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        availableCSSFileNames = listCssFiles();

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

    @FXML
    void loadProgramPressed(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Program");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Program File", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + UPLOAD_PROGRAM_RESOURCE))
                .newBuilder();
        String finalURL = urlBuilder.build().toString();

        // Building the body sent to the server to include the xml file
        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("program", selectedFile.getName(), RequestBody.create(selectedFile, MediaType.parse("multipart/form-data")))
                .build();

        // Building the request based on the body from above
        Request request = new Request.Builder()
                .url(finalURL)
                .addHeader("Cookie", "username=" + userNameDisplay.getText())
                .post(body)
                .build();

        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            currentLoadedProgramPath.setText(selectedFile.getAbsolutePath());
                        });

                    } else { //TODO: handle error by creating a new dialog window
                        System.out.println("Failed to send program to server: " + response.code());
                    }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void addCredits(ActionEvent event) {

        // Parse from text to int
        int chargeAmount = Integer.parseInt(creditsTextField.getText());

        // Build URL
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + ADD_CREDITS_RESOURCE))
                .newBuilder();
        String finalURL = urlBuilder.build().toString();

        // Create a temporal object containing the amount of credits to charge as json
        Gson gson = new Gson();
        String json = gson.toJson(Map.of("addCredits", chargeAmount));

        // Build the body sent to the server to include the creditRequest object
        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        // Building the request based on the body from above
        Request request = new Request.Builder()
                .url(finalURL)
                .addHeader("Cookie", "username=" + userNameDisplay.getText())
                .put(body)
                .build();

        // Send request
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

                } else { //TODO: handle error by creating a new dialog window
                    System.out.println("Failed to send program to server: " + response.code());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
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

    public void setUsername(String text) {
        userNameDisplay.setText(text);
    }

    public List<String> listCssFiles() {
        final String resourcePath = "css"; // adjust if your CSS ends up at a different path in the JAR
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
}