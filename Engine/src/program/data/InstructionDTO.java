package program.data;

import instruction.Instruction;

public class InstructionDTO {
    private final int number;
    private final String command;
    private final int cycles;
    private final VariableDTO variable;
    private final String label;
    private final String destinationLabel;
    private final String fullExpandedStringRepresentation;

    public String getFullExpandedStringRepresentation() {
        return fullExpandedStringRepresentation;
    }

    public InstructionDTO(Instruction instruction) {
        this.number = instruction.getNumber();
        this.command = instruction.toString();
        this.cycles = instruction.getCycles();
        this.label = instruction.getLabel().getLabelName();
        this.destinationLabel = instruction.getDestinationLabel().getLabelName();
        this.variable = new VariableDTO(instruction.getVariable());
        this.fullExpandedStringRepresentation = instruction.toString();
    }

    public int getNumber() {
        return number;
    }

    public String getCommand() {
        return command;
    }

    public int getCycles() {
        return cycles;
    }

    public VariableDTO getVariable() {
        return variable;
    }

    public String getLabel() {
        return label;
    }

    public String getDestinationLabel() {
        return destinationLabel;
    }
}