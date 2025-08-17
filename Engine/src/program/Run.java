package program;

import instruction.component.Variable;

import java.util.List;

public class Run {

    int runNumber;
    int runLevel;
    List<Variable> inputInts;
    int yValue;
    int runCycles;

    public Run(int runNumber, int runLevel, List<Variable> inputInts, int yValue, int runCycles) {
        this.runNumber = runNumber;
        this.runLevel = runLevel;
        this.inputInts = inputInts;
        this.yValue = yValue;
        this.runCycles = runCycles;
    }
}
