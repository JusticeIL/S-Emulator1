package execution.refreshTasks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.ProgramData;
import execution.controller.PrimaryController;
import jakarta.servlet.http.HttpServletResponse;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static configuration.ClientConfiguration.CLIENT;
import static configuration.DialogUtils.showAlert;
import static configuration.ResourcesConfiguration.*;

public class PullProgramInfoTask extends TimerTask {
    private final Button runButton;
    private final PrimaryController primaryController;

    public PullProgramInfoTask(Button runButton, PrimaryController primaryController) {
        this.runButton = runButton;
        this.primaryController = primaryController;
    }

    @Override
    public void run() {

        if (runButton.getScene().getWindow() == null) {
            cancel();
        }

        HttpUrl url = HttpUrl.parse(BASE_URL);
        List<Cookie> cookies = CLIENT.cookieJar().loadForRequest(Objects.requireNonNull(url));

        String username = cookies.stream()
                .filter(cookie -> "username".equals(cookie.name()))
                .findFirst()
                .map(Cookie::value)
                .orElse(null);

        if (username != null) {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + CHECK_EXECUTION_RESOURCE))
                    .newBuilder()
                    .addQueryParameter("username", username);
            String finalURL = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(finalURL)
                    .get()
                    .build();

            Call call = CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    try (ResponseBody body = response.body()) {
                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            JsonObject jsonObject = gson.fromJson(Objects.requireNonNull(body).string(), JsonObject.class);
                            if (jsonObject.has("status")) {
                                boolean isInExecution = jsonObject.get("status").getAsBoolean();
                                if (!isInExecution) {
                                    sendGetProgramRequest();
                                    cancel();
                                }
                            }

                        } else {
                            System.out.println("Failed to fetch if user is busy, code: " + response.code());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void sendGetProgramRequest() {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + PROGRAM_RESOURCE))
                .newBuilder();

        String finalURL = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(finalURL)
                .get()
                .build();

        Call call = CLIENT.newCall(request);
        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                if (response.code() == HttpServletResponse.SC_OK) {
                    try (ResponseBody responseBody = response.body()) {
                        Gson gson = new Gson();
                        primaryController.program = gson.fromJson(Objects.requireNonNull(responseBody).string(), ProgramData.class);
                        primaryController.getRightSideController().updateCycles();
                        primaryController.getRightSideController().updateResultVariableTable();
                        primaryController.getTopComponentController().sendUpdateCreditsRequest();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == HttpServletResponse.SC_NO_CONTENT) {
                    Stage primaryStage = (Stage) runButton.getScene().getWindow();
                    showAlert("No program data detected for the user.", primaryStage);
                }
            } else {
                Stage primaryStage = (Stage) runButton.getScene().getWindow();
                showAlert("Failed to retrieve program data. HTTP Code: " + response.code(), primaryStage);
            }
        } catch (Exception e) {
            Stage primaryStage = (Stage) runButton.getScene().getWindow();
            showAlert("Error: " + e.getMessage(), primaryStage);
        }
    }
}
