package program;

import instruction.Instruction;
import instruction.component.Label;
import instruction.component.Variable;
import dto.VariableDTO;
import program.function.FunctionInstance;
import user.User;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProgramExecutioner {

    private Program program;
    private Instruction currentInstruction;
    private int cycleCounter;
    private int currentCommandIndex;
    private int currentRunLevelForDebug;
    private boolean isDebugMode = false;
    private boolean isMainExecutioner = false;
    private boolean wasCalledFromFunction = false;
    private FunctionInstance callerFunctionInstance;
    private Map<String,Integer> xInitializedVariablesForDebug;
    private final Set<Integer> breakpoints = new HashSet<>();
    private User user;
    private int executionCost;

    private void executeSingleInstruction() {

        if(user!=null){
            if(user.getCredits() < currentInstruction.getCost()){
                executionCost += user.decreaseCredits(currentInstruction.getCost());
                program.setCycleCounter(cycleCounter);
                if(isDebugMode){
                    stopDebug();
                }
                return;
            }
            executionCost += user.decreaseCredits(currentInstruction.getCost());
        }
        Label nextLabel = currentInstruction.execute();
        cycleCounter += currentInstruction.getCycles();

        if (nextLabel.equals(Program.EMPTY_LABEL)) {
            currentCommandIndex++;
        } else if (nextLabel.equals(Program.EXIT_LABEL)) {
            currentCommandIndex = program.getInstructionList().size(); // Exit the loop
        } else {
            currentInstruction = program.getLabels().get(nextLabel);
            currentCommandIndex = currentInstruction.getNumber() - 1;
        }

        if (currentCommandIndex < program.getInstructionList().size()) {
            currentInstruction = program.getInstructionList().get(currentCommandIndex);
        }

        program.setCycleCounter(cycleCounter);
    }

    private void setUpNewRun(Set<VariableDTO> args){
        cycleCounter = 0;
        currentCommandIndex = 0;
        currentInstruction = program.getInstructionList().get(currentCommandIndex);

        Map<String,Variable> Variables = program.getVariables().stream().collect(Collectors.toMap(Variable::getName, Variable -> Variable));
        program.AddYVariableIfNotExists();
        for (Variable variable : Variables.values()) {
            variable.setValue(0);
        }

        // Check if variables and functions in args exist in program
        for (VariableDTO arg : args) {
            if (Variables.containsKey(arg.getName())) { // Case: variable argument
                Variables.get(arg.getName()).setValue(arg.getValue());
            } else if (arg.getName().startsWith("(") && arg.getName().endsWith(")")) { // Case: function argument
                // Ignore setup a function argument
            } else {
                throw new IllegalArgumentException("Argument " + arg.getName() + " not found in program variables and is not a function invoke.");
            }
        }

        // Handle negative variables
        args.stream()
                .filter(variable -> variable.getValue() < 0)
                .findAny()
                .ifPresent(var -> { throw new IllegalArgumentException("Variable " + var.getName() + " has a negative value: " + var.getValue());
                });

        this.currentCommandIndex = 0;
        this.cycleCounter = 0;
    }

    public void setMainExecutioner(User user) {
        isMainExecutioner = true;
        this.user = user;
    }

    public void setMainExecutioner() {
        isMainExecutioner = true;
    }


    public void setDebugMode(boolean debugMode) {
        isDebugMode = debugMode;
    }

    public void setProgram(Program program) {
        this.program = program;
        currentInstruction = program.getInstructionList().getFirst();
    }

    public void executeProgram(Set<VariableDTO> args) {
        if (program == null) {
            throw new IllegalStateException("Program not set. Please set a program before execution.");
        }
        setUpNewRun(args);

        Map<String,Integer> xInitializedVariables = program.getVariables().stream()
                .filter(var -> var.getName().startsWith("x"))
                .collect(Collectors.toMap(Variable::getName, Variable::getValue));
        int currentRunLevel = program.getCurrentProgramLevel();

        while (canContinueExecution()) {
            executeSingleInstruction();
        }

        Map<String,Integer> finalStateOfAllVariables = program.getVariables().stream()
                .collect(Collectors.toMap(Variable::getName, Variable::getValue));

        if (isMainExecutioner) {
            program.getStatistics().addRunToHistory(currentRunLevel, xInitializedVariables, finalStateOfAllVariables, cycleCounter);
            if(user != null) {
                user.getHistory().addRunToHistory(currentRunLevel, xInitializedVariables, finalStateOfAllVariables, cycleCounter);
            }
        }
        if (wasCalledFromFunction) {
            callerFunctionInstance.setCycles(cycleCounter);
        }
    }

    public void setUpDebugRun(Set<VariableDTO> args, Set<Integer> breakpoints) {
        setUpNewRun(args);
        this.breakpoints.clear();
        this.breakpoints.addAll(breakpoints);
        program.setNextInstructionIdForDebug(currentInstruction.getNumber());
        xInitializedVariablesForDebug = program.getVariables().stream()
                .filter(var -> var.getName().startsWith("x"))
                .collect(Collectors.toMap(Variable::getName, Variable::getValue));
        currentRunLevelForDebug = program.getCurrentProgramLevel();
        program.setInDebugMode(true);
        program.setCycleCounter(cycleCounter);
        if (!breakpoints.contains(currentInstruction.getNumber())) {
            resumeDebug();
        }
    }

    public void stepOver() {
        if(canContinueExecution()) {
            executeSingleInstruction();
            program.setNextInstructionIdForDebug(currentInstruction.getNumber());
        }
        if (!canContinueExecution() && isDebugMode) {
            stopDebug();
        }
    }

    public void stopDebug() {
        program.setInDebugMode(false);
        isDebugMode = false;
        Map<String,Integer> finalStateOfAllVariables = program.getVariables().stream()
                .collect(Collectors.toMap(Variable::getName, Variable::getValue));

        program.getStatistics().addRunToHistory(currentRunLevelForDebug, xInitializedVariablesForDebug, finalStateOfAllVariables, cycleCounter);
    }

    public void resumeDebug() {
        if(user == null || user.getCredits() > 0) {
            do {
                stepOver();
            } while (canContinueExecution());
        }
    }

    public void addBreakpoint(int lineNumber) {
        breakpoints.add(lineNumber);
    }

    public void removeBreakpoint(int lineNumber) {
        breakpoints.remove(lineNumber);
    }

    public void SetCallerFunctionInstance(FunctionInstance caller) {
        this.callerFunctionInstance = caller;
        this.wasCalledFromFunction = true;
    }

    private boolean canContinueExecution() {
        boolean conditionA = currentCommandIndex < program.getInstructionList().size();
        boolean conditionB = true;
        boolean conditionC = true;
        if(isMainExecutioner){
            conditionB = user.getCredits() >= currentInstruction.getCost();
        }
        if(isDebugMode){
           conditionC = !breakpoints.contains(currentInstruction.getNumber());
        }

        return conditionA && conditionB && conditionC;
    }

    public boolean isInDebug() {
        return isDebugMode;
    }

    public int getExecutionCost() {
        return executionCost;
    }
}