package instruction;

import XMLandJaxB.SInstruction;
import instruction.component.Label;
import instruction.component.Variable;

import java.util.List;

abstract public class SyntheticInstruction extends Instruction {

    protected List<Variable> variables;
    protected List<Instruction> instructions;

    public SyntheticInstruction(int num, List<Variable> variables, int cycles, Label label, Label destinationLabel) {
        super(num, cycles ,label, destinationLabel);
        this.variables = variables;
    }

    public List<Instruction> extend() { return instructions; }

    @Override
    public String toString() {
        return ("#" + number + " " + "S" + " " + "[" + label + "]" + " " + command + " " + cycles);
    }
}