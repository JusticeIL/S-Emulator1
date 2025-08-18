package instruction;

import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.List;

public class JumpEqualConstant extends SyntheticInstruction{

    static private final int CYCLES = 2;
    private final int constValue;

    public JumpEqualConstant(int num, List<Variable> variables, Label label, Label destinationLabel, int constValue) {
        super(num, variables, CYCLES, label, destinationLabel);
        if (variables.size() != 1) {
            throw new IllegalArgumentException("Jump Equal Constant Instruction must have exactly one variable"); // Temporal fix
        }
        command = "IF " + variables.getFirst().getName() + " = " + constValue + " GOTO " + destinationLabel.getLabelName();
        this.constValue = constValue;
    }

    @Override
    public Label execute() {
        if (variables.getFirst().getValue() == constValue) {
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
