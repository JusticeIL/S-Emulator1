package dashboard.refreshTasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dashboard.model.ProgramTableEntry;
import dto.ProgramData;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static configuration.ClientConfiguration.CLIENT;
import static configuration.ResourcesConfiguration.*;

public class ProgramsTableRefresher extends TimerTask {
    private final TableView<ProgramTableEntry> programsTable;

    public ProgramsTableRefresher(TableView<ProgramTableEntry> table) {
        this.programsTable = table;
    }

    @Override
    public void run() {
        // Only if we're on the dashboard we want to send scheduled requests
        if (programsTable.getScene().getWindow() != null) {
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + GET_ALL_PROGRAMS_RESOURCE))
                    .newBuilder();
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
                            String responseBody = Objects.requireNonNull(body).string();
                            Type type = new TypeToken<List<ProgramData>>() {
                            }.getType();
                            List<ProgramData> programs = gson.fromJson(responseBody, type);
                            List<ProgramTableEntry> fetchedPrograms = programs.stream()
                                    .map(ProgramTableEntry::new)
                                    //.sorted() TODO: decide sorting
                                    .toList();

                            Platform.runLater(() -> {
                                ObservableList<ProgramTableEntry> currentPrograms = programsTable.getItems();
                                Map<String, Integer> programNameToIndex = new HashMap<>();
                                for (int i = 0; i < currentPrograms.size(); i++) {
                                    programNameToIndex.put(currentPrograms.get(i).getProgramName(), i);
                                }

                                for (ProgramTableEntry fetchedProgram : fetchedPrograms) {
                                    String programName = fetchedProgram.getProgramName();
                                    Integer idx = programNameToIndex.get(programName);
                                    if (idx != null) {
                                        ProgramTableEntry currentProgram = currentPrograms.get(idx);
                                        if (!fetchedProgram.equals(currentProgram)) {
                                            currentPrograms.set(idx, fetchedProgram);
                                        }
                                    } else {
                                        currentPrograms.add(fetchedProgram);
                                    }
                                }
                            });

                        } else {
                            String responseBody = Objects.requireNonNull(body).string();
                            System.out.println("Failed to fetch program list: " + response.code());
                            System.out.println(responseBody);
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
