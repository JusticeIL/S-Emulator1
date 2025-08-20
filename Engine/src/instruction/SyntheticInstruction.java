package instruction;

import XMLandJaxB.SInstruction;
import instruction.component.Label;
import instruction.component.Variable;

import java.util.ArrayList;
import java.util.List;

abstract public class SyntheticInstruction extends Instruction {
    protected Variable variable;
    protected boolean isExpanded;
    protected List<Instruction> expandedInstructions;

    public SyntheticInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel) {
        super(num, cycles ,label, destinationLabel,InstructionType.S);
        this.variable = variable;
        this.isExpanded = false;
    }

}