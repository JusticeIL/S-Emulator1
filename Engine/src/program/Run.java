package program;

import instruction.component.Variable;

import java.util.List;

public class Run {

    private int runID;
    private int expansionLevel;
    private List<Variable> inputArgs;
    private int yValue;
    private int runCycles;

    public Run(int runNumber, int runLevel, List<Variable> inputInts, int yValue, int runCycles) {
        this.runID = runNumber;
        this.expansionLevel = runLevel;
        this.inputArgs = inputInts;
        this.yValue = yValue;
        this.runCycles = runCycles;
    }

    public int getRunID() {
        return runID;
    }

    public int getExpansionLevel() {
        return expansionLevel;
    }

    public List<Variable> getInputArgs() {
        return inputArgs;
    }

    public int getyValue() {
        return yValue;
    }

    public int getRunCycles() {
        return runCycles;
    }
}
