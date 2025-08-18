package instruction.synthetic;

import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.component.Label;
import instruction.component.Variable;

import java.util.List;

public class ConstantAssignment extends SyntheticInstruction {

    static private final int CYCLES = 2;
    private final int constValue;

    public ConstantAssignment(int num, List<Variable> variables, Label label, Label destinationLabel, int constValue) {
        super(num, variables, CYCLES, label, destinationLabel);
        if (variables.size() != 1) {
            throw new IllegalArgumentException("Constant Assignment Instruction must have exactly one variable"); // Temporal fix
        }
        command = variables.getFirst().getName() + " <- " + constValue;
        this.constValue = constValue;
    }

    @Override
    public Label execute() {
        variables.getFirst().setValue(constValue);
        return destinationLabel;
    }

    @Override
    public List<Instruction> expand() {
        return List.of();
    }
}
