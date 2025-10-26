package dashboard.refreshTasks;

import com.google.gson.Gson;
import dashboard.model.HistoryTableEntry;
import dashboard.model.UserTableEntry;
import dto.Statistics;
import dto.UserDTO;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static configuration.ClientConfiguration.CLIENT;
import static configuration.ResourcesConfiguration.*;

public class HistoryTableRefresher extends TimerTask {
    private final String username;
    private final TableView<HistoryTableEntry> historyTable;
    private final TableView<UserTableEntry> usersTable;

    public HistoryTableRefresher(TableView<HistoryTableEntry> table, String username, TableView<UserTableEntry> usersTable) {
        this.username = username;
        this.historyTable = table;
        this.usersTable = usersTable;
    }

    @Override
    public void run() {
        // Only if we're on the dashboard we want to send scheduled requests
        if (historyTable.getScene().getWindow() != null) {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + USER_RESOURCE))
                    .newBuilder()
                    .addQueryParameter("username", usersTable.getSelectionModel().getSelectedItem() != null ?
                            usersTable.getSelectionModel().getSelectedItem().getUsername() :
                            username);
            String finalURL = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(finalURL)
                    .get()
                    .build();

            Call call = CLIENT.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    try (response) {
                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            UserDTO user = gson.fromJson(Objects.requireNonNull(response.body()).string(), UserDTO.class);
                            Statistics userHistory = user.getHistory();
                            List<HistoryTableEntry> fetchedHistory = userHistory.getHistory().stream()
                                    .map(HistoryTableEntry::new)
                                    .sorted(Comparator.comparingInt(HistoryTableEntry::getRun))
                                    .toList();

                            Platform.runLater(() -> {
                                ObservableList<HistoryTableEntry> currentHistory = historyTable.getItems();
                                Map<Integer, Integer> runIDToIndex = new HashMap<>();
                                for (int i = 0; i < currentHistory.size(); i++) {
                                    runIDToIndex.put(currentHistory.get(i).getRun(), i);
                                }

                                for (HistoryTableEntry fetchedRun : fetchedHistory) {
                                    Integer runID = fetchedRun.getRun();
                                    Integer idx = runIDToIndex.get(runID);
                                    if (idx != null) {
                                        HistoryTableEntry currentRun = currentHistory.get(idx);
                                        if (!fetchedRun.equals(currentRun)) {
                                            currentHistory.set(idx, fetchedRun);
                                        }
                                    } else {
                                        currentHistory.add(fetchedRun);
                                    }
                                }
                            });

                        } else {
                            String body = Objects.requireNonNull(response.body()).string();
                            System.out.println("Failed to fetch function list: " + response.code());
                            System.out.println(body);
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
        } else {
            cancel();
        }
    }
}