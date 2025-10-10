package dashboard.controller;

import javafx.fxml.FXML;
import okhttp3.OkHttpClient;

public class PrimaryController {

    private OkHttpClient client;
    private static final String BASE_URL = "http://localhost:8080/S-emulator";
    private String username;

    @FXML
    private LeftSideController leftSideController;

    @FXML
    private RightSideController rightSideController;

    @FXML
    private TopComponentController topComponentController;

    @FXML
    public void initialize() {
        leftSideController.setRightController(rightSideController);
        leftSideController.setTopController(topComponentController);
        rightSideController.setLeftController(leftSideController);
        rightSideController.setTopController(topComponentController);
        topComponentController.setLeftController(leftSideController);
        topComponentController.setRightController(rightSideController);
    }

    public void initData(String text, OkHttpClient client) {
        this.username = text;
        topComponentController.setUsername(text);
        this.client = client;
    }

    public TopComponentController getTopComponentController() {
        return topComponentController;
    }
}