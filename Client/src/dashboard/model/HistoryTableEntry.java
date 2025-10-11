package dashboard.model;

import dto.ProgramType;
import dto.Run;
import instruction.ArchitectureGeneration;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class HistoryTableEntry {
    private final IntegerProperty run;
    private final ProgramType type;
    private final StringProperty programName;
    private final ArchitectureGeneration architectureType;
    private final IntegerProperty level;
    private final IntegerProperty y;
    private final IntegerProperty cycles;
    private final Map<String, Integer> allVariables;

    public HistoryTableEntry(Run run) {
        this.run = new SimpleIntegerProperty(run.getRunID());
        this.level = new SimpleIntegerProperty(run.getExpansionLevel());
        this.y = new SimpleIntegerProperty(run.getYValue());
        this.cycles = new SimpleIntegerProperty(run.getRunCycles());
        this.allVariables = new HashMap<>();
        allVariables.putAll(run.getFinalStateOfAllVariables());

        //TODO: change all of these to use real data from Run object
        type = ProgramType.Function;
        programName = new SimpleStringProperty("ExampleProgram");
        architectureType = ArchitectureGeneration.IV;
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
}