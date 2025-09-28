package program;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Run implements Serializable {

    private final int runCycles;
    private final int runID;
    private final int yValue;
    private final int expansionLevel;
    private final Map<String,Integer> inputArgs;
    private final Map<String,Integer> finalStateOfAllVariables;

    public Run(int runNumber, int runLevel, Map<String,Integer> inputArguments, Map<String,Integer> finalStateOfAllVariables, int runCycles) {
        this.finalStateOfAllVariables = new HashMap<>();
        this.finalStateOfAllVariables.putAll(finalStateOfAllVariables);
        this.inputArgs = new HashMap<>();
        inputArgs.putAll(inputArguments);
        this.runID = runNumber;
        this.expansionLevel = runLevel;
        this.yValue = finalStateOfAllVariables.get("y");
        this.runCycles = runCycles;
    }

    public int getRunID() {
        return runID;
    }

    public int getExpansionLevel() {
        return expansionLevel;
    }

    public Map<String, Integer> getInputArgs() {
        return inputArgs;
    }

    public int getYValue() {
        return yValue;
    }

    public int getRunCycles() {
        return runCycles;
    }

    public Map<String, Integer> getFinalStateOfAllVariables() {
        return finalStateOfAllVariables;
    }
}