package controller;

import XMLandJaxB.SFunction;
import XMLandJaxB.SProgram;

import java.util.HashMap;
import java.util.Map;

public class SharedProgramsContainer {
    private final Map<String, SProgram> sPrograms = new HashMap<>();
    private final Map<String, SFunction> sFunctions = new HashMap<>();
    private final Map<String,Integer> totalCreditsUsedPerProgram = new HashMap<>();
    private final Map<String,Integer> totalRunsPerProgram = new HashMap<>();

    public SProgram getSProgram(String programName) {
        return sPrograms.get(programName);
    }

    public void addSProgram(SProgram sProgram){
        sPrograms.putIfAbsent(sProgram.getName(),sProgram);
        sProgram.getSFunctions().getSFunction().forEach(sFunction -> {
            sFunctions.putIfAbsent(sFunction.getName(),sFunction);
        });
    }

    synchronized public void addToProgramTotalCost(String programName, int costForLastExecution) {
        totalCreditsUsedPerProgram.putIfAbsent(programName,0);
        totalRunsPerProgram.putIfAbsent(programName,0);
        totalCreditsUsedPerProgram.put(programName,totalCreditsUsedPerProgram.get(programName)+costForLastExecution);
        totalRunsPerProgram.put(programName,totalRunsPerProgram.get(programName)+1);
    }
}
