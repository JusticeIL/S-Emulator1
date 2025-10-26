package controller;

import program.Program;

import java.util.HashMap;
import java.util.Map;

public class ProgramContainer {
    private final Map<String,Map<String,Map<Integer, Program>>> mainContainer;
    private Map<String,Map<Integer,Program>> activeProgramContainer = new HashMap<>();
    private Map<Integer, Program> activeProgramExpansionsByLevel = new HashMap<>();

    public ProgramContainer() {
        this.mainContainer = new java.util.HashMap<>();
    }

    public void addProgram(Program program){
        if (mainContainer.containsKey(program.getProgramName())) {
            return;
        }

        Map<String,Map<Integer, Program>> singleProgramContainer = new HashMap<>();
        Map<Integer, Program> ProgramExpansions = new HashMap<>();
        ProgramExpansions.put(0,program);
        singleProgramContainer.put(program.getProgramName(),ProgramExpansions);

        program.getFunctions().forEach(function -> {
            HashMap<Integer,Program> functionExpansionMap = new HashMap<>();
            functionExpansionMap.put(0,function);
            singleProgramContainer.put(function.getProgramName(), functionExpansionMap);
        });

        mainContainer.put(program.getProgramName(),singleProgramContainer);
    }

    public Map<String, Map<Integer, Program>> getFullProgramContainer(String programName) {
        return mainContainer.get(programName);
    }

    public Map<Integer, Program> getProgramExpansions(String programName, String functionName) {
        return mainContainer.get(programName).get(functionName);
    }

    public void setActiveProgramContainer(String ProgramName) {
        activeProgramContainer = getFullProgramContainer(ProgramName);
    }

    public Map<Integer, Program> getProgramExpansions(String programName) {
        return mainContainer.get(programName).get(programName);
    }

    public Map<String, Map<Integer, Program>> getActiveProgramContainer() {
        return activeProgramContainer;
    }

    public Map<Integer, Program> getActiveProgramExpansionsByLevel() {
        return activeProgramExpansionsByLevel;
    }

    public Program ExpandProgram(String programName, int level) {
        int maxLevel = activeProgramExpansionsByLevel.get(0).getMaxProgramLevel();
        if (level > maxLevel) {
            throw new IllegalArgumentException("Level exceeds maximum program level of " + maxLevel);
        } else if (level < 0) {
            throw new IllegalArgumentException("Level is a negative number! the level number should be between 0 and " + activeProgramExpansionsByLevel.get(0).getMaxProgramLevel());
        }
        if (activeProgramExpansionsByLevel.containsKey(level)) {
            return activeProgramExpansionsByLevel.get(level);
        } else {
            Program expandedProgram = activeProgramExpansionsByLevel.get(0).expand(level);

            activeProgramExpansionsByLevel.put(level, expandedProgram);
            return expandedProgram;

        }
    }

    public void setActiveProgramExpansionsByLevel(String functionName) {
        activeProgramExpansionsByLevel = activeProgramContainer.get(functionName);
    }
}