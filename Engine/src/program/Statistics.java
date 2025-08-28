package program;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Statistics implements Serializable {

    private final List<Run> history;
    private int runCounter = 1;

    public Statistics() {
        history = new ArrayList<>();
    }

    public void addRunToHistory(int runLevel, Map<String,Integer> inputInts, int yValue, int runCycles) {
        history.add(new Run(runCounter++, runLevel, inputInts, yValue, runCycles));
    }

    public List<Run> getHistory() {
        return history;
    }
}