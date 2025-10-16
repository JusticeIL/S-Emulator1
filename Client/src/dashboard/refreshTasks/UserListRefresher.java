package dashboard.refreshTasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dashboard.model.UserTableEntry;
import dto.UserDTO;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class UserListRefresher extends TimerTask {
    private OkHttpClient client;
    private TableView<UserTableEntry> usersTable;
    private final String BASE_URL = "http://localhost:8080/S-emulator";
    private final String RESOURCE = "/api/users";
    private final int REFRESH_RATE = 1500; // in milliseconds

    public UserListRefresher(OkHttpClient client, TableView<UserTableEntry> table) {
        this.client = client;
        this.usersTable = table;
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

                    try (ResponseBody body = response.body()) {
                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            String responseBody = Objects.requireNonNull(body).string();
                            Type type = new TypeToken<Set<UserDTO>>() {
                            }.getType();
                            Set<UserDTO> users = gson.fromJson(responseBody, type);
                            List<UserTableEntry> fetchedUsers = users.stream()
                                    .map(UserTableEntry::new)
                                    .sorted() // Sorted users by lexicographical order of usernames
                                    .toList();

                            Platform.runLater(() -> {
                                ObservableList<UserTableEntry> currentUsers = usersTable.getItems();
                                Map<String, Integer> usernameToIndex = new HashMap<>();
                                for (int i = 0; i < currentUsers.size(); i++) {
                                    usernameToIndex.put(currentUsers.get(i).getUsername(), i);
                                }

                                for (UserTableEntry fetchedUser : fetchedUsers) {
                                    String username = fetchedUser.getUsername();
                                    Integer idx = usernameToIndex.get(username);
                                    if (idx != null) {
                                        UserTableEntry currentUser = currentUsers.get(idx);
                                        if (!fetchedUser.equals(currentUser)) {
                                            currentUsers.set(idx, fetchedUser);
                                        }
                                    } else {
                                        currentUsers.add(fetchedUser);
                                    }
                                }
                            });

                        } else { //TODO: handle error by creating a new dialog window
                            System.out.println("Failed to fetch user list: " + response.code());
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
}