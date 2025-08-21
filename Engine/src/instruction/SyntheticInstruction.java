package instruction;

import instruction.component.Label;
import instruction.component.Variable;
import program.InstructionExecutioner;

abstract public class SyntheticInstruction extends Instruction {
    protected Variable variable;
    protected boolean isExpanded;
    protected ExpandedSyntheticInstructionArguments expandedInstruction;

    public SyntheticInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel) {
        super(num, cycles ,label, destinationLabel,InstructionType.S);
        this.variable = variable;
        this.isExpanded = false;
    }

    @Override
    public Label execute() {
        if(isExpanded){
            return InstructionExecutioner.executeInstructions(expandedInstruction.getInstructions(), expandedInstruction.getLabels());
        }
        return executeUnExpandedInstruction();
    }

    protected abstract Label executeUnExpandedInstruction();
}