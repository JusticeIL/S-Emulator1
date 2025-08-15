package Engine;

import Engine.XMLandJaxB.SInstruction;

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

    public abstract Label execute();
    // Implementation of command execution logic
    private final Map<String, Label> Labels = new TreeMap<>();
    public Map<String, Label> getLabels() {
        return Labels;
    }
    public abstract List<Instruction> expand();

    abstract public String toString();

    public Instruction(SInstruction sInstruction, int num, int cycles) {
        this.number = num;
        String LabelName = sInstruction.getSInstructionArguments().getSInstructionArgument() //Problem: not sure we're getting one argument!
                .getFirst().
                getValue();
        if (Labels.containsKey(LabelName)) {
            this.label = Labels.get(LabelName);
        }
        else {
              this.label = new Label(LabelName);
              Labels.put(LabelName, this.label);
            }
        this.cycles = cycles; //Implement
        //this.destinationLabel = sInstruction.getSInstructionArguments().getSInstructionArgument();
        this.level = 0; // Implement
        this.command = sInstruction.getName();
        }

    public int getNumber() {
        return number;
    }

    protected void updateCycles(int cycles) {
        this.cycles = cycles;
    }
}
