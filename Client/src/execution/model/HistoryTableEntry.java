package execution.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import program.Run;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryTableEntry {
    private final IntegerProperty run;
    private final IntegerProperty level;
    private final StringProperty args;
    private final IntegerProperty y;
    private final IntegerProperty cycles;
    private final Map<String, Integer> allVariables;

    public HistoryTableEntry(Run run) {
        this.run = new SimpleIntegerProperty(run.getRunID());
        this.level = new SimpleIntegerProperty(run.getExpansionLevel());
        this.args = new SimpleStringProperty(run.getInputArgs().entrySet().stream()
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining(", ", "[", "]")));
        this.y = new SimpleIntegerProperty(run.getYValue());
        this.cycles = new SimpleIntegerProperty(run.getRunCycles());
        this.allVariables = new HashMap<>();
        allVariables.putAll(run.getFinalStateOfAllVariables());
    }

    // getters and setters (needed by PropertyValueFactory)
    public int getRun() { return run.get(); }
    public void setRun(int runID) { run.set(runID); }
    public IntegerProperty runProperty() { return run; }

    public int getLevel() { return level.get(); }
    public void setLevel(int expansionLevel) { level.set(expansionLevel); }
    public IntegerProperty levelProperty() { return level; }

    public String getArgs() { return args.get(); }
    public void setArgs(String arguments) { args.set(arguments); }
    public StringProperty argsProperty() { return args; }

    public int getY() { return y.get(); }
    public void setY(int yValue) { y.set(yValue); }
    public IntegerProperty yProperty() { return y; }

    public int getCycles() { return cycles.get(); }
    public void setCycles(int newCycles) { cycles.set(newCycles); }
    public IntegerProperty cyclesProperty() { return cycles; }

    public Map<String, Integer> getAllVariables() {
        return allVariables;
    }
}