package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.JumpNotZero;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.ArrayList;
import java.util.List;

public class JumpZero extends SyntheticInstruction {

    static private final int CYCLES = 2;

    public JumpZero(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = "IF " + variable.getName() + " =0" + " GOTO " + destinationLabel.getLabelName();
    }

    @Override
    public Label execute() {
        if (variable.getValue() == 0) {
            return destinationLabel;
        } else {
            return Program.EMPTY_LABEL; // No jump, handle later
        }
    }

    @Override
    public ExpandedSyntheticInstructionArguments expand() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Label L1 = new Label();
        expandedInstructions.add(new JumpNotZero(number, variable, Program.EMPTY_LABEL, L1));
        expandedInstructions.add(new GoToLabel(number, variable, Program.EMPTY_LABEL, destinationLabel));
        expandedInstructions.add(new Neutral(number, variable, L1, Program.EMPTY_LABEL)); // variable should be y
        return expandedInstructions;
    }
}
