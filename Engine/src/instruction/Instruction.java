package instruction;

import instruction.component.Label;
import java.io.Serializable;
import java.util.List;

abstract public class Instruction implements Executable, Expandable, Serializable {

    protected String command;
    protected int cycles;
    protected Label label;
    protected Label destinationLabel;
    protected int level;
    protected int number;
    protected final String DELIMITER = ">>>";
    protected final InstructionType instructionType;
    protected final Instruction parentInstruction;

    public abstract Label execute(); // Implementation of command execution logic

    public Label getLabel() {
        return label;
    }

    public Label getDestinationLabel() {
        return destinationLabel;
    }

    public abstract ExpandedSyntheticInstructionArguments generateExpandedInstructions();

    public int getLevel() {
        return level;
    }

    public Instruction(int num, int cycles, Label label, Label destinationLabel, InstructionType instructionType) {
        this.number = num;
        this.label = label;
        this.cycles = cycles;
        this.destinationLabel = destinationLabel;
        this.instructionType = instructionType;
        this.level = 0;
        this.parentInstruction = null;
    }

    public Instruction(int num, int cycles, Label label, Label destinationLabel, InstructionType instructionType, Instruction parentInstruction) {
        this.number = num;
        this.label = label;
        this.cycles = cycles;
        this.destinationLabel = destinationLabel;
        this.instructionType = instructionType;
        this.level = 0;
        this.parentInstruction = parentInstruction;
    }

    public int getNumber() {
        return number;
    }

    public int getCycles() {
        return cycles;
    }

    @Override
    public String toString() {
        String thisInstructionString = "#" + String.format("%-3s", number) + " " + "(" + instructionType + ")" + " " + "[" + String.format(" %-4s", label) + "]" + " " + String.format("%-24s", command) + " " + "(" + cycles + ")";
        if(parentInstruction != null) {
            thisInstructionString += " " + DELIMITER + " " + parentInstruction;
        }
        return thisInstructionString;
    }

    public abstract List<String> getExpandedStringRepresentation();

    public void setNumber(int number) {
        this.number = number;
    }
}