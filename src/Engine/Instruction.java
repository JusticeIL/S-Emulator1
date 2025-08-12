package Engine;

import Engine.XMLandJaxB.SInstruction;

import java.util.List;

abstract public class Instruction implements Executable, Expandable {

    private int number;
    private String label;
    private String command;
    private int cycles;
    private String destinationLabel;
    private int level;

    public abstract String execute();
        // Implementation of command execution logic

    public abstract List<Instruction> expand();

    abstract public String toString();

}