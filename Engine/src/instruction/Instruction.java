package instruction;

import instruction.component.Label;
import instruction.component.Variable;
import instruction.component.LabelFactory;
import instruction.component.VariableFactory;
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
    protected Instruction parentInstruction;
    protected final Variable variable;
    protected Variable argumentVariable;

    public InstructionType getInstructionType() {
        return instructionType;
    }

    public String getCommand() {
        return command;
    }

    public abstract Label execute(); // Implementation of command execution logic

    public Label getLabel() {
        return label;
    }

    public Label getDestinationLabel() {
        return destinationLabel;
    }

    public abstract ExpandedSyntheticInstructionArguments generateExpandedInstructions(LabelFactory labelFactory, VariableFactory variableFactory);

    public int getLevel() {
        return level;
    }

    public Instruction(int num, int cycles, Label label, Label destinationLabel, InstructionType instructionType, Variable variable) {
        this.number = num;
        this.label = label;
        this.cycles = cycles;
        this.destinationLabel = destinationLabel;
        this.instructionType = instructionType;
        this.level = 0; // Gets updated in inherited instructions
        this.parentInstruction = null;
        this.variable = variable;
        this.argumentVariable = variable;
    }

    public Instruction(int num, int cycles, Label label, Label destinationLabel, InstructionType instructionType, Variable variable, Instruction parentInstruction) {
        this.number = num;
        this.label = label;
        this.cycles = cycles;
        this.destinationLabel = destinationLabel;
        this.instructionType = instructionType;
        this.level = 0; // Gets updated in inherited instructions
        this.variable = variable;
        this.argumentVariable = variable;
        this.parentInstruction = parentInstruction;
    }

    public void setParentInstruction(Instruction parentInstruction) {
        this.parentInstruction = parentInstruction;
    }

    public Variable getArgumentVariable() {
        return argumentVariable;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCycles() {
        return cycles;
    }
  
    public Variable getVariable() {
        return variable;
    }

    @Override
    public String toString() {
        String thisInstructionString = "#" + String.format("%-3s", number) + " " + "(" + instructionType + ")" + " " + "[" + String.format(" %-4s", label) + "]" + " " + String.format("%-24s", command) + " " + "(" + cycles + ")";
        if(parentInstruction != null) {
            thisInstructionString += " " + DELIMITER + " " + parentInstruction;
        }
        return thisInstructionString;
    }

    public Instruction getParentInstruction() {
        return parentInstruction;
    }

    public abstract List<String> getExpandedStringRepresentation();

    abstract public Instruction duplicate(Variable newVariable, Variable newArgumentVariable, Label newLabel, Label newDestinationLabel);
}