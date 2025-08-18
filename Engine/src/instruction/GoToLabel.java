package instruction;

import instruction.component.Label;
import instruction.component.Variable;

import java.util.List;

public class GoToLabel extends SyntheticInstruction {

    static private final int CYCLES = 1;

    public GoToLabel(int num, List<Variable> variables, Label label, Label destinationLabel) {
        super(num, variables, CYCLES, label, destinationLabel);
        command = "GOTO " + destinationLabel.getLabelName();
    }

    @Override
    public Label execute() {
        return destinationLabel;
    }

    @Override
    public List<Instruction> expand() {
        return List.of();
    }
}
