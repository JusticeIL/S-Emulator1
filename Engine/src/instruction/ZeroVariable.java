package instruction;

import XMLandJaxB.SInstruction;
import instruction.component.Label;
import instruction.component.Variable;

import java.util.List;

public class ZeroVariable extends SyntheticInstruction {

    static private final int CYCLES = 1;

    public ZeroVariable(int num, List<Variable> variables, Label label, Label destinationLabel) {
        super(num, variables, CYCLES, label, destinationLabel);
        if (variables.size() != 1) {
            throw new IllegalArgumentException("Zero Variable Instruction must have exactly one variable"); // Temporal fix
        }
        command = variables.getFirst().getName() + " <- " + "0";
    }

    @Override
    public Label execute() {
        variables.getFirst().setValue(0);
        return destinationLabel;
    }

    @Override
    public List<Instruction> expand() {
        return List.of();
    }
}
