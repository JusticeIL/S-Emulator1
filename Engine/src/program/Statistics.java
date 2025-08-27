package program;

import instruction.component.Variable;

import java.util.ArrayList;
import java.util.List;

public class Statistics {

    private final List<Run> history;
    int runCounter = 1;


    public Statistics() {
        history = new ArrayList<>();
    }

    public void addRunToHistory(int runLevel, List<Variable> inputInts, int yValue, int runCycles) {
        history.add(new Run(runCounter++, runLevel, inputInts, yValue, runCycles));
    }


    public void addRunToHistory(Run run) {
        history.add(run);
    }

    public List<Run> getHistory() {
        return history;
    }
}