package dashboard.refreshTasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.UserDTO;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;
import java.util.TimerTask;

public class UserListRefresher extends TimerTask {
    private OkHttpClient client;
    private final String BASE_URL = "http://localhost:8080/S-emulator";
    private final String RESOURCE = "/api/users";
    private final int REFRESH_RATE = 1500; // in milliseconds

    public UserListRefresher(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        if (client != null) {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE))
                    .newBuilder();
            String finalURL = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(finalURL)
                    .get()
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        String responseBody = response.body().string();
                        Type type = new TypeToken<Set<UserDTO>>() {}.getType();
                        Set<UserDTO> users = gson.fromJson(responseBody, type);
                        Platform.runLater(() -> {
                            System.out.println("Fetched users: " + users);
                        });
                    } else { //TODO: handle error by creating a new dialog window
                        System.err.println("Failed to fetch user list: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
