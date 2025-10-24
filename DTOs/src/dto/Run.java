package dto;

import program.Program;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Run implements Serializable {

    private final int runID;
    // private final ProgramType programType; TODO: ctor needs to be adapted
    private final String programType;
    private final String programName;// TODO: ctor needs to be adapted
    private final ArchitectureGeneration architectureGeneration; //TODO: ctor needs to be adapted
    private final int expansionLevel;
    private final int yValue;
    private final int runCycles;


    private final Map<String,Integer> inputArgs;
    private final Map<String,Integer> finalStateOfAllVariables;

    public Run(int runNumber, int runLevel, Map<String,Integer> inputArguments, Map<String,Integer> finalStateOfAllVariables, int runCycles, String programType, String programName, ArchitectureGeneration architectureGeneration) {
        this.finalStateOfAllVariables = new HashMap<>();
        this.finalStateOfAllVariables.putAll(finalStateOfAllVariables);
        this.inputArgs = new HashMap<>();
        inputArgs.putAll(inputArguments);
        this.runID = runNumber;
        this.expansionLevel = runLevel;
        this.yValue = finalStateOfAllVariables.get("y");
        this.runCycles = runCycles;
        this.programType = programType;
        this.programName = programName;
        this.architectureGeneration = architectureGeneration;
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

    public String getProgramType() {
        return programType;
    }

    public String getProgramName() {
        return programName;
    }

    public ArchitectureGeneration getArchitectureGeneration() {
        return architectureGeneration;
    }
}