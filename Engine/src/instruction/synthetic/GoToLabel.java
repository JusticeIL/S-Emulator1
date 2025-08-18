package instruction.synthetic;

import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.component.Label;
import instruction.component.Variable;

import java.util.List;

public class GoToLabel extends SyntheticInstruction {

    static private final int CYCLES = 1;

    public GoToLabel(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES, label, destinationLabel);
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
