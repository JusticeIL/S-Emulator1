package instruction;

import instruction.component.Label;
import instruction.component.Variable;
import instruction.component.LabelFactory;
import instruction.component.VariableFactory;
import program.function.FunctionArgument;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

abstract public class Instruction implements Executable, Expandable, Serializable {

    protected String command;
    protected int cycles;
    protected Label label;
    protected Label destinationLabel;
    protected int level;
    protected int number;
    protected Instruction parentInstruction;
    protected final String DELIMITER = ">>>";
    protected final InstructionType instructionType;
    protected final Variable variable;
    protected Variable argumentVariable;
    protected ArchitectureGeneration architecture;

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

    public InstructionType getInstructionType() {
        return instructionType;
    }

    public String getCommand() {
        return command;
    }

    public Label getLabel() {
        return label;
    }

    public Label getDestinationLabel() {
        return destinationLabel;
    }

    public int getLevel() {
        return level;
    }

    public void setParentInstruction(Instruction parentInstruction) {
        this.parentInstruction = parentInstruction;
    }

    public FunctionArgument getArgumentVariable() {
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

    public String getCyclesStringRepresentation() {
        return String.valueOf(cycles);
    }

    public Instruction getParentInstruction() {
        return parentInstruction;
    }

    public List<FunctionArgument> getInnerFunctionVariables() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        String thisInstructionString = "#" + String.format("%-3s", number) + " " + "(" + instructionType + ")" + " " + "[" + String.format(" %-4s", label) + "]" + " " + String.format("%-24s", command) + " " + "(" + cycles + ")";
        if(parentInstruction != null) {
            thisInstructionString += " " + DELIMITER + " " + parentInstruction;
        }
        return thisInstructionString;
    }

    public String getArchitecture(){
        return architecture.toString();
    }

    public int getCost(){
        return architecture.getCost();
    }

    abstract public Instruction duplicate(Variable newVariable, Variable newArgumentVariable, Label newLabel, Label newDestinationLabel);
    abstract public Label execute(); // Implementation of command execution logic
    abstract public ExpandedSyntheticInstructionArguments generateExpandedInstructions(LabelFactory labelFactory, VariableFactory variableFactory);
    abstract public List<String> getExpandedStringRepresentation();
}