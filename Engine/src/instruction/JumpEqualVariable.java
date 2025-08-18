package instruction;

import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.List;

public class JumpEqualVariable extends SyntheticInstruction {

    static private final int CYCLES = 2;

    public JumpEqualVariable(int num, List<Variable> variables, Label label, Label destinationLabel) {
        super(num, variables, CYCLES, label, destinationLabel);
        if (variables.size() != 2) {
            throw new IllegalArgumentException("Jump Equal Variable Instruction must have exactly two variables"); // Temporal fix
        }
        command = command = "IF " + variables.getFirst().getName() + " = " + variables.get(1).getName() + " GOTO " + destinationLabel.getLabelName();;
    }

    @Override
    public Label execute() {
        if (variables.getFirst().getValue() == variables.get(1).getValue()) {
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
