package execution.controller;

import com.google.gson.Gson;
import controller.SingleProgramController;
import dto.ProgramData;
import jakarta.servlet.http.HttpServletResponse;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import okhttp3.*;

import java.util.Objects;

import static configuration.ClientConfiguration.CLIENT;
import static configuration.DialogUtils.showAlert;
import static configuration.ResourcesConfiguration.*;

public class PrimaryController {

    public ProgramData program;

    @FXML
    private ScrollPane root;

    @FXML
    private LeftSideController leftSideController;

    @FXML
    private RightSideController rightSideController;

    @FXML
    private TopComponentController topComponentController;

    @FXML
    public void initialize() {
        program = null;

        SingleProgramController singleProgramController = new SingleProgramController();
        leftSideController.setRightController(rightSideController);
        leftSideController.setTopController(topComponentController);
        rightSideController.setLeftController(leftSideController);
        rightSideController.setTopController(topComponentController);
        topComponentController.setLeftController(leftSideController);
        topComponentController.setRightController(rightSideController);
        topComponentController.setPrimaryController(this);
        leftSideController.setPrimaryController(this);
        rightSideController.setPrimaryController(this);

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + PROGRAM_RESOURCE))
                .newBuilder();

        String finalURL = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(finalURL)
                .get()
                .build();

        Call call = CLIENT.newCall(request);

        new Thread(() -> {
            try (Response response = call.execute()) {
                if (response.isSuccessful()) {
                    if (response.code() == HttpServletResponse.SC_OK) {
                        try (ResponseBody responseBody = response.body()) {
                            Gson gson = new Gson();
                            this.program = gson.fromJson(Objects.requireNonNull(responseBody).string(), ProgramData.class);
                            topComponentController.initAllFields();
                            leftSideController.initAllFields();
                            rightSideController.initAllFields();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == HttpServletResponse.SC_NO_CONTENT) {
                        Stage primaryStage = (Stage) root.getScene().getWindow();
                        showAlert("No program data detected for the user.", primaryStage);
                    }
                } else {
                    Stage primaryStage = (Stage) root.getScene().getWindow();
                    showAlert("Failed to retrieve program data. HTTP Code: " + response.code(), primaryStage);
                }
            } catch (Exception e) {
                Stage primaryStage = (Stage) root.getScene().getWindow();
                showAlert("Error: " + e.getMessage(), primaryStage);
            }
        }).start();
    }

    public TopComponentController getTopComponentController() {
        return topComponentController;
    }
}