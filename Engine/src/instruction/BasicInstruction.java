package instruction;

import instruction.component.Label;
import instruction.component.Variable;

import java.util.*;

abstract public class BasicInstruction extends Instruction {

    protected Variable variable;

    public ExpandedSyntheticInstructionArguments expand() {
        Set<Variable> newVariables = new HashSet<>();
        Map<Label,Instruction> newLabels = new HashMap<>();
        return new ExpandedSyntheticInstructionArguments(newVariables, newLabels);
    }

    public BasicInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel) {
        super(num, cycles, label, destinationLabel, InstructionType.B);
        this.variable = variable;
    }


}
