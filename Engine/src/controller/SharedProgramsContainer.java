package controller;

import XMLandJaxB.SFunction;
import XMLandJaxB.SFunctions;
import XMLandJaxB.SProgram;
import dto.ProgramData;
import program.Program;
import program.function.Function;
import program.function.FunctionsContainer;
import user.User;

import java.io.FileNotFoundException;
import java.util.*;

public class SharedProgramsContainer {
    private final Map<String, SProgram> sPrograms = new HashMap<>();
    private final Map<String, SFunction> sFunctions = new HashMap<>();
    private final Map<String,Integer> totalCreditsUsedPerProgram = new HashMap<>();
    private final Map<String,Integer> totalRunsPerProgram = new HashMap<>();
    private final Map<String, Program> dummyProgramsForDashboard = new HashMap<>();
    private final FunctionsContainer sharedFunctionsContainer = new FunctionsContainer();

    public SProgram getSProgram(String programName) {
        return sPrograms.get(programName);
    }

   synchronized public void addSProgram(SProgram sProgram, String username){
        sPrograms.putIfAbsent(sProgram.getName(),sProgram);
        Program dummyProgram = new Program(sProgram,new User(username));
        dummyProgramsForDashboard.putIfAbsent(sProgram.getName(),dummyProgram);
        dummyProgram.setUploadingUser(username);
        Optional<SFunctions> sFunctionsOpt = Optional.ofNullable(sProgram.getSFunctions());
        sFunctionsOpt.ifPresent(programSFunctions -> {
             programSFunctions.getSFunction().forEach(sFunction -> {
                 sFunctions.putIfAbsent(sFunction.getName(),sFunction);
             });
             sharedFunctionsContainer.setup(sFunctions.values());
        });
        dummyProgram.getFunctions().forEach(function -> {
            dummyProgramsForDashboard.putIfAbsent(function.getName(),function);
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
        String SearchableName = getSearchableName(programName);
        return new ProgramData(dummyProgramsForDashboard.get(SearchableName));
    }

    private String getSearchableName(String name){
        return getAllFunctionNames().contains(name)?sFunctions.get(name).getUserString():name;
    }

    public Collection<String> getAllProgramNames() {
        return sPrograms.keySet();
    }

    public Collection<String> getAllFunctionNames() {
        return sFunctions.keySet();
    }

    public int getNumberOfFunctions(String username) {
        List<String> allFunctionNames = new ArrayList<>(getAllFunctionNames());
        int count = 0;
        for(String functionName : allFunctionNames) {
            String functionUserString = getSearchableName(functionName);
            if(dummyProgramsForDashboard.get(functionUserString).getUploadingUser().equals(username)) {
                count++;
            }
        }
        return count;
    }

    public Set<SFunction> getAllSFunctions() {
        return new HashSet<>(sFunctions.values());
    }

    public SFunction getSFunctions(String programName) {
        return sFunctions.get(programName);
    }

    public void addSFunction(SFunction sFunction, String username) {
        try {
            sFunctions.putIfAbsent(sFunction.getName(), sFunction);
            Program dummyProgram = null;
            dummyProgram = new Function(sFunction, sharedFunctionsContainer,new User(username));
            dummyProgramsForDashboard.putIfAbsent(sFunction.getName(), dummyProgram);
            dummyProgram.setUploadingUser(username);
            dummyProgram.getFunctions().forEach(function -> {
                dummyProgramsForDashboard.putIfAbsent(function.getName(), function);
            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
