package instruction.synthetic;

import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Increase;
import instruction.basic.JumpNotZero;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.ArrayList;
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
        List<Instruction> expandedInstructions = new ArrayList<>();
        Variable z1 = new Variable();
        expandedInstructions.add(new Increase(number,z1, label, Program.EMPTY_LABEL));
        expandedInstructions.add(new JumpNotZero(number,z1,Program.EMPTY_LABEL, destinationLabel));
        return expandedInstructions;
    }
}
