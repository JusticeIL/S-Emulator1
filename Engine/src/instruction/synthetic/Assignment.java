package instruction.synthetic;

import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.component.Label;
import instruction.component.Variable;

import java.util.ArrayList;
import java.util.List;

public class Assignment extends SyntheticInstruction {

    static private final int CYCLES = 4;
    Variable argumentVariable;

    public Assignment(int num, Variable assignedVariable, Label label, Label destinationLabel, Variable argumentVariable) {
        super(num, assignedVariable, CYCLES, label, destinationLabel);
        this.argumentVariable = argumentVariable;
        command = assignedVariable.getName() + " <- " + argumentVariable.getName();
    }

    @Override
    public Label execute() {
        variable.setValue(argumentVariable.getValue());
        return destinationLabel;
    }

    @Override
    public List<Instruction> expand() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        return expandedInstructions;

    }
}
