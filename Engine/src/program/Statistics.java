package program;

import java.util.ArrayList;
import java.util.List;

public class Statistics {

    List<Run> history;

    public Statistics() {
        history = new ArrayList<>();
    }

    public void addRunToHistory(Run run) {
        history.add(run);
    }
}