package instruction;

import XMLandJaxB.SInstruction;
import instruction.component.Label;
import instruction.component.Variable;

import java.util.List;

abstract public class SyntheticInstruction extends Instruction {
    protected Variable variable;

    public SyntheticInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel) {
        super(num, cycles ,label, destinationLabel);
        this.variable = variable;
    }

    @Override
    public String toString() {
        return ("#" + number + " " + "S" + " " + "[" + label + "]" + " " + command + " " + "(" +cycles + ")");
    }
}