package instruction;

import instruction.component.Label;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

abstract public class Instruction implements Executable, Expandable {

    protected int number;
    protected Label label;
    protected int cycles;
    protected Label destinationLabel;
    protected String command;
    protected int level;
    protected final InstructionType instructionType;
    protected final Instruction parentInstruction;
    protected final String DELIMITER = ">>>";

    public abstract Label execute();
    // Implementation of command execution logic
    private final Map<String, Label> Labels = new TreeMap<>();

    public Label getLabel() {
        return label;
    }

    public Label getDestinationLabel() {
        return destinationLabel;
    }

    public Map<String, Label> getLabels() {
        return Labels;
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
        this.level = 0; // Implement
        this.parentInstruction = null;
    }

    public Instruction(int num, int cycles, Label label, Label destinationLabel, InstructionType instructionType, Instruction parentInstruction) {
        this.number = num;
        this.label = label;
        this.cycles = cycles;
        this.destinationLabel = destinationLabel;
        this.instructionType = instructionType;
        this.level = 0; // Implement
        this.parentInstruction = parentInstruction;
    }


    public int getNumber() {
        return number;
    }

    protected void updateCycles(int cycles) {
        this.cycles = cycles;
    }

    public int getCycles() {
        return cycles;
    }

    @Override
    public String toString() {
        String thisInstructionString = "#" + number + " " + "(" + instructionType + ")" + " " + "[" + String.format(" %-4s", label) + "]" + " " + command + " " + "(" + cycles + ")";
        if(parentInstruction != null) {
            thisInstructionString += " " + DELIMITER + " " + parentInstruction.toString(); // Are we sure we need the parentheses?
        }
        return thisInstructionString;
    }

    public abstract void revertExpansion();

    public abstract List<String> getExpandedStringRepresentation();

    public void setNumber(int number) {
        this.number = number;
    }
}
