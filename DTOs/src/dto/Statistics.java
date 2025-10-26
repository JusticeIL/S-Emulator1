package dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Statistics implements Serializable {

    private int runCounter = 1;
    private final List<Run> history;

    public Statistics() {
        history = new ArrayList<>();
    }

    public void addRunToHistory(Run run) {
        history.add(run);
        runCounter++;
    }

    public List<Run> getHistory() {
        return history;
    }
}