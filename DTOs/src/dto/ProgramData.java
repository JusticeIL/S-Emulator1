package dto;

import instruction.Instruction;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;
import program.Statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private final int nextInstructionIdForDebug;
    private final boolean isDebugmode;
    private final List<String> functionNames;

    public ProgramData(Program program) {
        this.programName = program.getProgramName();
        for (Instruction instruction : program.getInstructionList()) {
            programInstructions.add(new InstructionDTO(instruction));
        }
        for (Instruction instruction: program.getRuntimeExecutedInstructions()){
            runtimeExecutedInstructions.add(instruction.toString());
        }

        for (Variable variable : program.getVariables()) {
            programVariablesCurrentState.add(new VariableDTO(variable));
        }
        programVariablesCurrentState.sort((a, b) -> {
            // Assign priority: y=0, x=1, z=2, others=3
            String nameA = a.getName();
            String nameB = b.getName();

            int priorityA = nameA.startsWith("y") ? 0 : (nameA.startsWith("x") ? 1 : (nameA.startsWith("z") ? 2 : 3));
            int priorityB = nameB.startsWith("y") ? 0 : (nameB.startsWith("x") ? 1 : (nameB.startsWith("z") ? 2 : 3));
            if (priorityA != priorityB) {
                return Integer.compare(priorityA, priorityB);
            }

            int numA = extractNumber(nameA);
            int numB = extractNumber(nameB);

            if (numA != numB) {
                return Integer.compare(numA, numB);
            }
            return nameA.compareTo(nameB);
        });

        this.programXArguments.addAll(program.getUsedXVariableNames());

        for (Label label : program.getLabelNames()) {
            programLabels.add(label.toString());
        }
        this.maxExpandLevel = program.getMaxProgramLevel();
        this.currentCycles = program.getProgramCycles();
        this.statistics = program.getStatistics();
        this.nextInstructionIdForDebug = program.getNextInstructionIdForDebug();
        this.isDebugmode = program.isInDebugMode();
        this.functionNames = new ArrayList<>();
        functionNames.add(program.getProgramName()); // Add primary program name
        program.getFunctions().forEach(function -> functionNames.add(function.getUserString())); // Add all function user-representation strings
    }

    // Helper method for comparing variable names with numerical suffixes
    private int extractNumber(String name) {
        String digits = name.replaceAll("^[a-zA-Z]+", "");
        if (digits.isEmpty()) { // Case: no number, sort after numbered variables
            return Integer.MAX_VALUE;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    public boolean isDebugmode() {
        return isDebugmode;
    }

    public String getProgramName() {
        return programName;
    }

    public int getNextInstructionIdForDebug() {
        return nextInstructionIdForDebug;
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

    public int getCurrentCycles() {
        return currentCycles;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public List<String> getAllFunctionNames() {
        return functionNames;
    }
}