package program;

import instruction.component.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Run {

    private final int runID;
    private final int expansionLevel;
    private final Map<String,Integer> inputArgs;
    private final int yValue;
    private final int runCycles;

    public Run(int runNumber, int runLevel, List<Variable> inputInts, int yValue, int runCycles) {
        this.inputArgs = new HashMap<>();
        this.runID = runNumber;
        this.expansionLevel = runLevel;
        this.yValue = yValue;
        this.runCycles = runCycles;
        
        for(Variable var : inputInts) {
            inputArgs.put(var.getName(), var.getValue());
        }
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

    public int getyValue() {
        return yValue;
    }

    public int getRunCycles() {
        return runCycles;
    }
}
