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
    private Map<String, Label> Labels = new TreeMap<>();
    public Map<String, Label> getLabels() {
        return Labels;
    }
    public abstract List<Instruction> expand();

    abstract public String toString();

    public Instruction(SInstruction sInstruction, int num) {
        this.number = num;
        String LabelName = sInstruction.getSInstructionArguments().getSInstructionArgument();
        if (Labels.containsKey(LabelName)) {
            this.label = Labels.get(LabelName);
        }
        else {
              this.label = new Label(this.LabelName);
              Labels.put(());
            }
        this.cycles = 0; //Implement
        //this.destinationLabel = sInstruction.getSInstructionArguments().getSInstructionArgument();
        this.level = 0; // Implement
        this.command = sInstruction.getName();
        }
    }
