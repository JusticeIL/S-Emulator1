package program;

import instruction.Instruction;
import instruction.component.Label;
import instruction.component.Variable;

import java.util.ArrayList;
import java.util.List;

public final class ProgramData {
    private final String programName;
    private final int maxExpandLevel;
    private final int currentCycles;
    private final List<String> programXArguments = new ArrayList<>();
    private final List<String> programVariablesCurrentState = new ArrayList<>();
    private final List<String> programLabels = new ArrayList<>();
    private final List<String> programInstructions = new ArrayList<>();
    private final List<String> runtimeExecutedInstructions = new ArrayList<>(); // TODO: Implement
    private final List<String> expandedProgramInstructions = new ArrayList<>();
    private final Statistics statistics;

    public ProgramData(Program program) {
        this.programName = program.getProgramName();
        for (Instruction instruction : program.getInstructionList()) {
            programInstructions.add(instruction.toString());
        }
        for(Instruction instruction: program.getRuntimeExecutedInstructions()){
            runtimeExecutedInstructions.add(instruction.toString());
        }

        for (Variable variable : program.getVariables()) {
            if(variable.getName().contains("x")){
                programXArguments.add(variable.getName());
            }
            programVariablesCurrentState.add(variable.toString());
        }

        programVariablesCurrentState.sort((a, b) -> {
            // Assign priority: y=0, x=1, z=2, others=3
            int priorityA = a.startsWith("y") ? 0 : (a.startsWith("x") ? 1 : (a.startsWith("z") ? 2 : 3));
            int priorityB = b.startsWith("y") ? 0 : (b.startsWith("x") ? 1 : (b.startsWith("z") ? 2 : 3));
            if (priorityA != priorityB) {
                return Integer.compare(priorityA, priorityB);
            }
            return a.compareTo(b);
        });

        for (Label label : program.getLabels()) {
            programLabels.add(label.toString());
        }
        expandedProgramInstructions.addAll(program.getExpandedProgramStringRepresentation());
        this.maxExpandLevel = program.getMaxProgramLevel();
        this.currentCycles = program.getProgramCycles();
        this.statistics = program.getStatistics();
    }

    public List<String> getExpandedProgramInstructions() {
        return expandedProgramInstructions;
    }

    public String getProgramName() {
        return programName;
    }

    public List<String> getProgramXArguments() {
        return programXArguments;
    }

    public List<String> getProgramLabels() {
        return programLabels;
    }

    public List<String> getProgramVariablesCurrentState() {
        return programVariablesCurrentState;
    }

    public List<String> getProgramInstructions() {
        return programInstructions;
    }

    public int getMaxExpandLevel() {
        return maxExpandLevel;
    }

    public List<String> getRuntimeExecutedInstructions() {
        return runtimeExecutedInstructions;
    }

    public int getCurrentCycles() {
        return currentCycles;
    }

    public Statistics getStatistics() {
        return statistics;
    }
}
