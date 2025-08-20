package instruction;

import instruction.component.Label;
import instruction.component.Variable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract public class BasicInstruction extends Instruction {

    protected Variable variable;

    public ExpandedSyntheticInstructionArguments expand() {
        Set<Variable> newVariables = new HashSet<>();
        Set<Label> newLabels = new HashSet<>();
        return new ExpandedSyntheticInstructionArguments(newVariables, newLabels);
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
