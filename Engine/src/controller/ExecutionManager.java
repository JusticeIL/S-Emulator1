package controller;

import dto.VariableDTO;
import program.Program;
import program.ProgramExecutioner;
import user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ExecutionManager {

    private final Map<User, ProgramExecutioner> executioners = new HashMap<>();
    private final Map<User,Boolean> isCurrentlyInDebugMode = new HashMap<>();
    private final Map<User,Integer> costForLastExecution = new HashMap<>();

    public void runProgram(User user, Set<VariableDTO> args) {
        Program activeProgram = user.getActiveProgram();
        ProgramExecutioner programExecutioner = new ProgramExecutioner();
        executioners.put(user, programExecutioner);
        isCurrentlyInDebugMode.put(user, false);
        programExecutioner.setMainExecutioner(user);
        programExecutioner.setProgram(activeProgram);
        programExecutioner.executeProgram(args);
        costForLastExecution.put(user,programExecutioner.getExecutionCost());
        executioners.remove(user);
    }

    public void startDebug(User user, Set<VariableDTO> args,Set<Integer> breakpoints) {
        Program activeProgram = user.getActiveProgram();
        ProgramExecutioner programExecutioner = new ProgramExecutioner();
        programExecutioner.setDebugMode(true);
        programExecutioner.setProgram(activeProgram);
        programExecutioner.setMainExecutioner(user);
        programExecutioner.setUpDebugRun(args, breakpoints);
        isCurrentlyInDebugMode.put(user, true);
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
            checkForEndOfDebug(user);
        }
    }

    private void checkForEndOfDebug(User user) {
        Optional<ProgramExecutioner> executionerOpt = Optional.ofNullable(executioners.get(user));
        executionerOpt.ifPresent(executioner -> {
            if(!executioner.isInDebug()) {
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
}
