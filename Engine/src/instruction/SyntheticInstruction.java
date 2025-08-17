package instruction;

import XMLandJaxB.SInstruction;
import instruction.component.Label;
import instruction.component.Variable;

import java.util.List;

abstract public class SyntheticInstruction extends Instruction {

    List<Variable> variables;
    protected List<Instruction> instructions;

    public SyntheticInstruction(SInstruction sInstruction, int num, Label label, Label destinationLabel) {

        super(num, 0,label, destinationLabel );
    }

    public List<Instruction> extend() { return instructions; }

    @Override
    public String toString() {
        return ("#" + number + " " + "S" + " " + "[" + label + "]" + " " + command + " " + cycles);
    }
}