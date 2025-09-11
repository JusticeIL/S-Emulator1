package program.data;

import instruction.Instruction;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;
import program.Statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ProgramData implements Serializable {

    private final int currentCycles;
    private final int maxExpandLevel;
    private final List<InstructionDTO> programInstructions = new ArrayList<>();
    private final List<String> programLabels = new ArrayList<>();
    private final String programName;
    private final List<VariableDTO> programVariablesCurrentState = new ArrayList<>();
    private final List<String> programXArguments = new ArrayList<>();
    private final List<String> runtimeExecutedInstructions = new ArrayList<>();
    private final Statistics statistics;

    public ProgramData(Program program) {
        this.programName = program.getProgramName();
        for (Instruction instruction : program.getInstructionList()) {
            programInstructions.add(new InstructionDTO(instruction));
        }
        for(Instruction instruction: program.getRuntimeExecutedInstructions()){
            runtimeExecutedInstructions.add(instruction.toString());
        }

        for (Variable variable : program.getVariables()) {
            programVariablesCurrentState.add(new VariableDTO(variable));
        }
        programVariablesCurrentState.sort((a, b) -> {
            // Assign priority: y=0, x=1, z=2, others=3
            int priorityA = a.getName().startsWith("y") ? 0 : (a.getName().startsWith("x") ? 1 : (a.getName().startsWith("z") ? 2 : 3));
            int priorityB = b.getName().startsWith("y") ? 0 : (b.getName().startsWith("x") ? 1 : (b.getName().startsWith("z") ? 2 : 3));
            if (priorityA != priorityB) {
                return Integer.compare(priorityA, priorityB);
            }
            return a.getName().compareTo(b.getName());
        });

        this.programXArguments.addAll(program.getUsedXVariableNames());

        for (Label label : program.getLabels()) {
            programLabels.add(label.toString());
        }
        this.maxExpandLevel = program.getMaxProgramLevel();
        this.currentCycles = program.getProgramCycles();
        this.statistics = program.getStatistics();
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

    public List<VariableDTO> getProgramVariablesCurrentState() {
        return programVariablesCurrentState;
    }

    public List<String> getProgramInstructionsForConsole() {
        return programInstructions.stream().map(InstructionDTO::getFullExpandedStringRepresentation).toList();
    }

    public List<InstructionDTO> getProgramInstructions() {
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