package instruction;

import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.List;

public class JumpZero extends SyntheticInstruction {

    static private final int CYCLES = 2;

    public JumpZero(int num, List<Variable> variables, Label label, Label destinationLabel) {
        super(num, variables, CYCLES, label, destinationLabel);
        if (variables.size() != 1) {
            throw new IllegalArgumentException("Jump Zero Instruction must have exactly one variable"); // Temporal fix
        }
        command = "IF " + variables.getFirst().getName() + " =0" + " GOTO " + destinationLabel.getLabelName();
    }

    @Override
    public Label execute() {
        if (variables.getFirst().getValue() == 0) {
            return destinationLabel;
        } else {
            return Program.EMPTY_LABEL; // No jump, handle later
        }
    }

    @Override
    public List<Instruction> expand() {
        return List.of();
    }
}
