package instruction.synthetic;

import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.List;

public class JumpEqualVariable extends SyntheticInstruction {

    static private final int CYCLES = 2;
    private final Variable argumentVariable;

    public JumpEqualVariable(int num, Variable variable, Label label, Label destinationLabel, Variable argumentVariable) {
        super(num, variable, CYCLES, label, destinationLabel);
        this.argumentVariable = argumentVariable;
        command = command = "IF " + variable.getName() + " = " + argumentVariable.getName() + " GOTO " + destinationLabel.getLabelName();;
    }

    @Override
    public Label execute() {
        if (variable.getValue() == argumentVariable.getValue()) {
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
