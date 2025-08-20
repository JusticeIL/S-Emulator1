package instruction;

import instruction.component.Label;

import java.util.Map;
import java.util.TreeMap;

abstract public class Instruction implements Executable, Expandable {

    protected int number;
    protected Label label;
    protected int cycles;
    protected Label destinationLabel;
    protected String command;
    protected int level;

    public abstract Label execute();
    // Implementation of command execution logic
    private final Map<String, Label> Labels = new TreeMap<>();

    public Label getLabel() {
        return label;
    }

    public Map<String, Label> getLabels() {
        return Labels;
    }
    public abstract ExpandedSyntheticInstructionArguments expand();

    abstract public String toString();

    public Instruction(int num, int cycles, Label label, Label destinationLabel) {
        this.number = num;
        this.label = label;
        this.cycles = cycles;
        this.destinationLabel = destinationLabel;
        this.level = 0; // Implement
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
}
