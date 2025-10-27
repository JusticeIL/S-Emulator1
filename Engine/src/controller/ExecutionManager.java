package controller;

import dto.VariableDTO;
import dto.ArchitectureGeneration;
import program.Program;
import program.ProgramExecutioner;
import user.User;

import javax.naming.InsufficientResourcesException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ExecutionManager {

    private final Map<User, ProgramExecutioner> executioners = new HashMap<>();
    private final Map<User,Boolean> isCurrentlyInDebugMode = new HashMap<>();
    private final Map<User,Integer> costForLastExecution = new HashMap<>();
    private final Map<User,Boolean> lastProgramExecutionFailed = new HashMap<>();

    public void runProgram(User user, Set<VariableDTO> args, ArchitectureGeneration architecture) {
        Program activeProgram = user.getActiveProgram();

        if (architecture.getCost()<activeProgram.getMinimalArchitectureNeededForExecution().getCost()) {
            throw new InvalidParameterException("Tried running level " +
                    activeProgram.getMinimalArchitectureNeededForExecution() +
                    " program in "+ architecture + " architecture");
        }
        ProgramExecutioner programExecutioner = new ProgramExecutioner();
        executioners.put(user, programExecutioner);
        isCurrentlyInDebugMode.put(user, false);
        programExecutioner.setMainExecutioner(user, architecture);
        programExecutioner.setProgram(activeProgram);
        programExecutioner.executeProgram(args);
        costForLastExecution.put(user,programExecutioner.getExecutionCost());
        checkProgramExecutionFailedOnCredits(user);
        executioners.remove(user);
    }

    public void startDebug(User user, Set<VariableDTO> args,Set<Integer> breakpoints, ArchitectureGeneration architecture) {
        Program activeProgram = user.getActiveProgram();

        if(architecture.getCost()<activeProgram.getMinimalArchitectureNeededForExecution().getCost()){
            throw new InvalidParameterException("Tried running level " +
                    activeProgram.getMinimalArchitectureNeededForExecution() +
                    " program in "+ architecture + " architecture");
        }
        ProgramExecutioner programExecutioner = new ProgramExecutioner();
        executioners.put(user, programExecutioner);
        programExecutioner.setDebugMode(true);
        programExecutioner.setProgram(activeProgram);
        programExecutioner.setMainExecutioner(user, architecture);
        isCurrentlyInDebugMode.put(user, true);
        programExecutioner.setUpDebugRun(args, breakpoints);
        checkProgramExecutionFailedOnCredits(user);
        checkForEndOfDebug(user);
    }

    public void addBreakpoint(User user, int lineNumber) {
        Optional<ProgramExecutioner> executionerOpt = Optional.ofNullable(executioners.get(user));
        executionerOpt.ifPresent(executioner -> executioner.addBreakpoint(lineNumber));
    }

    public void removeBreakpoint(User user, int lineNumber) {
        Optional<ProgramExecutioner> executionerOpt = Optional.ofNullable(executioners.get(user));
        executionerOpt.ifPresent(executioner -> executioner.removeBreakpoint(lineNumber));
    }

    public void stepOver(User user) {
        if(isCurrentlyInDebugMode.get(user)) {
            Optional<ProgramExecutioner> executionerOpt = Optional.ofNullable(executioners.get(user));
            executionerOpt.ifPresent(ProgramExecutioner::stepOver);
            checkProgramExecutionFailedOnCredits(user);
            checkForEndOfDebug(user);
        }
    }

    private void checkForEndOfDebug(User user) {
        Optional<ProgramExecutioner> executionerOpt = Optional.ofNullable(executioners.get(user));
        executionerOpt.ifPresent(executioner -> {
            if(!executioner.isInDebug()||executioner.isProgramExecutionFailed()) {
                isCurrentlyInDebugMode.put(user, false);
                costForLastExecution.put(user,executioner.getExecutionCost());
                executioners.remove(user);
            }
        });
    }

    public void stopDebug(User user) {
        if(isCurrentlyInDebugMode.get(user)) {
            Optional<ProgramExecutioner> executionerOpt = Optional.ofNullable(executioners.get(user));
            executionerOpt.ifPresent(executioner -> {
                executioner.stopDebug();
                executioner.setDebugMode(false);
                isCurrentlyInDebugMode.put(user, false);
                executioners.remove(user);
            });


        }
    }

    public void resumeDebug(User user) {
        Optional<ProgramExecutioner> executionerOpt = Optional.ofNullable(executioners.get(user));
        executionerOpt.ifPresent(ProgramExecutioner::resumeDebug);
        checkProgramExecutionFailedOnCredits(user);
        checkForEndOfDebug(user);
    }

    public void setDebugMode(User user, boolean isDebugMode) {
        isCurrentlyInDebugMode.put(user, isDebugMode);
    }

    public int getCostForLastExecution(User user) {
        return costForLastExecution.get(user);
    }

    public boolean isInDebugMode(User user) {
        return isCurrentlyInDebugMode.getOrDefault(user, false);
    }

    public void CheckForCreditsAboveProgramAverage(User user, ArchitectureGeneration architecture, SharedProgramsContainer sharedProgramsContainer) throws InsufficientResourcesException {
        float avgCost = sharedProgramsContainer.getAvgCost(user.getActiveProgram().getProgramName());
        if (user.getCredits()-architecture.getCost()<avgCost) {
            throw new InsufficientResourcesException("User credit total, less than active program average cost");
        }
    }

    private void checkProgramExecutionFailedOnCredits(User user){
        lastProgramExecutionFailed.put(user,executioners.get(user).isProgramExecutionFailed());
    }

    public boolean checkInsufficientCredits(User user) {

        return lastProgramExecutionFailed.getOrDefault(user, false);
    }

    public boolean isInExecution(User user) {
        return executioners.containsKey(user);
    }
}