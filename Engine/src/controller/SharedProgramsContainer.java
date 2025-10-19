package controller;

import XMLandJaxB.SFunction;
import XMLandJaxB.SFunctions;
import XMLandJaxB.SProgram;
import dto.ProgramData;
import program.Program;
import program.function.FunctionsContainer;

import java.util.*;

public class SharedProgramsContainer {
    private final Map<String, SProgram> sPrograms = new HashMap<>();
    private final Map<String, SFunction> sFunctions = new HashMap<>();
    private final Map<String,Integer> totalCreditsUsedPerProgram = new HashMap<>();
    private final Map<String,Integer> totalRunsPerProgram = new HashMap<>();
    private final Map<String, Program> dummyProgramsForDashboard = new HashMap<>();

    public SProgram getSProgram(String programName) {
        return sPrograms.get(programName);
    }

   synchronized public void addSProgram(SProgram sProgram, String username){
        sPrograms.putIfAbsent(sProgram.getName(),sProgram);
        Program dummyProgram = new Program(sProgram,new FunctionsContainer());//TODO:REMOVE SHARED FUNCTIONS CONTAINER
        dummyProgramsForDashboard.putIfAbsent(sProgram.getName(),dummyProgram);
        dummyProgram.setUploadingUser(username);
        dummyProgram.getFunctions().forEach(function -> {dummyProgramsForDashboard.putIfAbsent(function.getName(),dummyProgram);});
       Optional<SFunctions> sFunctionsOpt = Optional.ofNullable(sProgram.getSFunctions());
         sFunctionsOpt.ifPresent(programSFunctions -> {
             programSFunctions.getSFunction().forEach(sFunction -> {
                 sFunctions.putIfAbsent(sFunction.getName(),sFunction);
                 Program dummyFunction = new Program(sProgram,new FunctionsContainer());//TODO:REMOVE SHARED FUNCTIONS CONTAINER
                 dummyProgramsForDashboard.putIfAbsent(sProgram.getName(),dummyFunction);
                 dummyFunction.setUploadingUser(username);
             });
        });
    }

    public void addRunForProgram(String programName,int costForLastExecution) {
        Program program = dummyProgramsForDashboard.get(programName);
        synchronized (program) {
            program.updateNumberOfRuns();
            program.updateCostOfAllRuns(costForLastExecution);
        }
    }

    synchronized public void addToProgramTotalCost(String programName, int costForLastExecution) {
        totalCreditsUsedPerProgram.putIfAbsent(programName,0);
        totalRunsPerProgram.putIfAbsent(programName,0);
        totalCreditsUsedPerProgram.put(programName,totalCreditsUsedPerProgram.get(programName)+costForLastExecution);
        totalRunsPerProgram.put(programName,totalRunsPerProgram.get(programName)+1);
    }

    public ProgramData getSharedProgramData(String programName) {
        return new ProgramData(dummyProgramsForDashboard.get(programName));
    }

    public Collection<String> getAllProgramNames() {
        return sPrograms.keySet();
    }

    public Collection<String> getAllFunctionNames() {
        return sFunctions.keySet();
    }

    public int getNumberOfFunctions(String username) {
        return (int) getAllFunctionNames().stream()
                .filter(functionName->dummyProgramsForDashboard.
                        get(functionName).getUploadingUser().equals(username)).
                count();

    }
}
