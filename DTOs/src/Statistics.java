import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Statistics implements Serializable {

    private int runCounter = 1;
    private final List<Run> history;

    public Statistics() {
        history = new ArrayList<>();
    }

    public void addRunToHistory(int runLevel, Map<String,Integer> inputInts, Map<String,Integer> finalStateOfAllVariables, int runCycles) {
        history.add(new Run(runCounter++, runLevel, inputInts, finalStateOfAllVariables, runCycles));
    }

    public List<Run> getHistory() {
        return history;
    }
}