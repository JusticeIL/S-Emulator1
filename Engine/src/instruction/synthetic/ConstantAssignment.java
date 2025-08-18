package instruction.synthetic;

import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.component.Label;
import instruction.component.Variable;

import java.util.List;

public class ConstantAssignment extends SyntheticInstruction {

    static private final int CYCLES = 2;
    private final int constValue;

    public ConstantAssignment(int num, Variable variable, Label label, Label destinationLabel, int constValue) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = variable.getName() + " <- " + constValue;
        this.constValue = constValue;
    }

    @Override
    public Label execute() {
        variable.setValue(constValue);
        return destinationLabel;
    }

    @Override
    public List<Instruction> expand() {
        return List.of();
    }
}
