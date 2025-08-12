package Engine;

import Engine.XMLandJaxB.SInstruction;

import java.util.List;

abstract public class Instruction implements Executable, Expandable {

    protected int number;
    protected String label;
    protected int cycles;
    protected String destinationLabel;
    protected String command;
    protected int level;

    public abstract String execute();
        // Implementation of command execution logic

    public abstract List<Instruction> expand();

    abstract public String toString();

    public Instruction(SInstruction sInstruction, int num) {
        this.number = num;
        this.label = sInstruction.getSLabel();
        this.cycles = 0; //Implement
        this.destinationLabel = sInstruction.getSLabel();
        this.level = 0; // Implement
    }
}