package instruction;

import instruction.component.Label;
import instruction.component.Variable;

import java.util.ArrayList;
import java.util.List;

abstract public class BasicInstruction extends Instruction {

    protected Variable variable;

    public List<Instruction> expand() {
        List<Instruction> res = new ArrayList<>(1);
        res.add(this);

        return res;
    }

    public BasicInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel) {
        super(num, cycles, label, destinationLabel);
        this.variable = variable;
    }

    @Override
    public String toString() {
        return ("#" + number + " " + "B" + " " + "[" + label + "]" + " " + command + " " + "(" + cycles + ")");
    }
}
