package program;

import instruction.Instruction;
import instruction.component.Label;
import instruction.component.Variable;
import program.data.VariableDTO;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProgramExecutioner {
    private Program program;
    private Instruction currentInstruction;
    private int cycleCounter;
    private int currentCommandIndex;
    private boolean isDebugMode = false;
    private Map<String,Integer> xInitializedVariablesForDebug;
    int currentRunLevelForDebug;

    public boolean isDebugMode() {
        return isDebugMode;
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

        while (currentCommandIndex < program.getInstructionList().size()) {
            executeSingleInstruction();
        }

        Map<String,Integer> finalStateOfAllVariables = program.getVariables().stream()
                .collect(Collectors.toMap(Variable::getName, Variable::getValue));

        program.getStatistics().addRunToHistory(currentRunLevel, xInitializedVariables, finalStateOfAllVariables, cycleCounter);
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
        for (VariableDTO arg : args) {
            if (Variables.containsKey(arg.getName())) {
                Variables.get(arg.getName()).setValue(arg.getValue());
            } else {
                throw new IllegalArgumentException("Argument " + arg.getName() + " not found in program variables.");
            }
        }
        this.currentCommandIndex = 0;
        this.cycleCounter = 0;
    }

    public void setUpDebugRun(Set<VariableDTO> args){
        setUpNewRun(args);
        program.setNextInstructionIdForDebug(currentInstruction.getNumber());
        xInitializedVariablesForDebug = program.getVariables().stream()
                .filter(var -> var.getName().startsWith("x"))
                .collect(Collectors.toMap(Variable::getName, Variable::getValue));
        currentRunLevelForDebug = program.getCurrentProgramLevel();
        program.setInDebugMode(true);
        program.setCycleCounter(cycleCounter);
    }

    private void executeSingleInstruction() {
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

    public void stepOver() {
        executeSingleInstruction();
        program.setNextInstructionIdForDebug(currentInstruction.getNumber());
        if(currentCommandIndex >= program.getInstructionList().size() && isDebugMode) {
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
        while (currentCommandIndex < program.getInstructionList().size()) {
            executeSingleInstruction();
        }
        stopDebug();
    }
}
