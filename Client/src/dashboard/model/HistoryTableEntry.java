package dashboard.model;

import dto.ProgramType;
import dto.Run;
import dto.ArchitectureGeneration;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryTableEntry {
    private final IntegerProperty run;
    private final ProgramType type;
    private final StringProperty programName;
    private final ArchitectureGeneration architectureType;
    private final IntegerProperty level;
    private final IntegerProperty y;
    private final IntegerProperty cycles;
    private final Map<String, Integer> allVariables;
    private final StringProperty args;

    public HistoryTableEntry(Run run) {
        this.run = new SimpleIntegerProperty(run.getRunID());
        this.level = new SimpleIntegerProperty(run.getExpansionLevel());
        this.y = new SimpleIntegerProperty(run.getYValue());
        this.cycles = new SimpleIntegerProperty(run.getRunCycles());
        this.allVariables = new HashMap<>();
        allVariables.putAll(run.getFinalStateOfAllVariables());
        this.type = ProgramType.valueOf(run.getProgramType());
        this.programName = new SimpleStringProperty(run.getProgramName());
        this.architectureType = run.getArchitectureGeneration();
        this.args = new SimpleStringProperty(run.getInputArgs().entrySet().stream()
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining(", ", "[", "]")));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoryTableEntry)) return false;
        HistoryTableEntry other = (HistoryTableEntry) o;
        return Integer.valueOf(this.run.get()).equals(other.run.get()) &&
                this.type.equals(other.type) &&
                this.programName.get().equals(other.programName.get()) &&
                this.architectureType.equals(other.architectureType) &&
                Integer.valueOf(this.level.get()).equals(other.level.get()) &&
                Integer.valueOf(this.y.get()).equals(other.y.get()) &&
                Integer.valueOf(this.cycles.get()).equals(other.cycles.get());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(
                run.get(),
                type,
                programName.get(),
                architectureType,
                level.get(),
                y.get(),
                cycles.get()
        );
    }

    // getters and setters (needed by PropertyValueFactory)
    public int getRun() { return run.get(); }
    public void setRun(int runID) { run.set(runID); }
    public IntegerProperty runProperty() { return run; }

    public ProgramType getType() { return type; }

    public String getProgramName() { return programName.get(); }
    public void setProgramName(String name) { programName.set(name); }
    public StringProperty programNameProperty() { return programName; }

    public ArchitectureGeneration getArchitectureType() { return architectureType; }

    public int getLevel() { return level.get(); }
    public void setLevel(int expansionLevel) { level.set(expansionLevel); }
    public IntegerProperty levelProperty() { return level; }

    public int getY() { return y.get(); }
    public void setY(int yValue) { y.set(yValue); }
    public IntegerProperty yProperty() { return y; }

    public int getCycles() { return cycles.get(); }
    public void setCycles(int newCycles) { cycles.set(newCycles); }
    public IntegerProperty cyclesProperty() { return cycles; }

    public Map<String, Integer> getAllVariables() {
        return allVariables;
    }

    public String getArgs() { return args.get(); }
    public void setArgs(String arguments) { args.set(arguments); }
    public StringProperty argsProperty() { return args; }
}