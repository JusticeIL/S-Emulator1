package dashboard.refreshTasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dashboard.model.FunctionTableEntry;
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

public class FunctionsTableRefresher extends TimerTask {
    private final TableView<FunctionTableEntry> functionsTable;

    public FunctionsTableRefresher(TableView<FunctionTableEntry> table) {
        this.functionsTable = table;
    }

    @Override
    public void run() {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + GET_ALL_FUNCTIONS_RESOURCE))
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
                        List<ProgramData> functions = gson.fromJson(responseBody, type);
                        List<FunctionTableEntry> fetchedFunctions = functions.stream()
                                .map(FunctionTableEntry::new)
                                //.sorted() TODO: decide sorting
                                .toList();

                        Platform.runLater(() -> {
                            ObservableList<FunctionTableEntry> currentFunctions = functionsTable.getItems();
                            Map<String, Integer> functionNameToIndex = new HashMap<>();
                            for (int i = 0; i < currentFunctions.size(); i++) {
                                functionNameToIndex.put(currentFunctions.get(i).getFunctionName(), i);
                            }

                            for (FunctionTableEntry fetchedFunction : fetchedFunctions) {
                                String functionName = fetchedFunction.getFunctionName();
                                Integer idx = functionNameToIndex.get(functionName);
                                if (idx != null) {
                                    FunctionTableEntry currentFunction = currentFunctions.get(idx);
                                    if (!fetchedFunction.equals(currentFunction)) {
                                        currentFunctions.set(idx, fetchedFunction);
                                    }
                                } else {
                                    currentFunctions.add(fetchedFunction);
                                }
                            }
                        });

                    } else {
                        String responseBody = Objects.requireNonNull(body).string();
                        System.out.println("Failed to fetch function list: " + response.code());
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
