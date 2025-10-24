package program;

import dto.ArchitectureGeneration;
import dto.Run;
import dto.Statistics;

import java.util.Map;

public class RunBuilder {

    private int runCycles;
    private int expansionLevel;
    private Map<String,Integer> inputArgs;
    private Map<String,Integer> finalStateOfAllVariables;
    private String programType;
    private String programName;
    private ArchitectureGeneration architectureGeneration;

    public RunBuilder setRunCycles(int runCycles) {
        this.runCycles = runCycles;
        return this;
    }

    public RunBuilder setProgramType(String programType) {
        this.programType = programType;
        return this;
    }

    public RunBuilder setProgramName(String programName) {
        this.programName = programName;
        return this;
    }

    public RunBuilder setArchitectureGeneration(ArchitectureGeneration architectureGeneration) {
        this.architectureGeneration = architectureGeneration;
        return this;
    }

    public RunBuilder setExpansionLevel(int expansionLevel) {
        this.expansionLevel = expansionLevel;
        return this;
    }

    public RunBuilder setInputArgs(Map<String, Integer> inputArgs) {
        this.inputArgs = inputArgs;
        return this;
    }

    public RunBuilder setFinalStateOfAllVariables(Map<String, Integer> finalStateOfAllVariables) {
        this.finalStateOfAllVariables = finalStateOfAllVariables;
        return this;
    }

    public dto.Run build(Statistics statistics) {
        return new Run(statistics.getHistory().size()+1, expansionLevel, inputArgs, finalStateOfAllVariables, runCycles, programType, programName, architectureGeneration);
        }
}
