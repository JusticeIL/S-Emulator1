package instruction.synthetic;

import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Decrease;
import instruction.basic.Increase;
import instruction.basic.JumpNotZero;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.ArrayList;
import java.util.List;

public class JumpEqualConstant extends SyntheticInstruction {

    static private final int CYCLES = 2;
    private final int constValue;

    public JumpEqualConstant(int num, Variable variable, Label label, Label destinationLabel, int constValue) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = "IF " + variable.getName() + " = " + constValue + " GOTO " + destinationLabel.getLabelName();
        this.constValue = constValue;
    }

    @Override
    public Label execute() {
        if (variable.getValue() == constValue) {
            return destinationLabel;
        } else {
            return Program.EMPTY_LABEL; // No jump, handle later
        }
    }

    @Override
    public List<Instruction> expand() { // Waiting for answer from Aviad
        List<Instruction> expandedInstructions = new ArrayList<>();
        Label L1 = new Label();
        Variable z1 = new Variable();
        expandedInstructions.add(new Assignment(number, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL, variable));
        for (int i = 0 ; i<constValue; i++) { // It looks disgusting in lambda
            expandedInstructions.add(new JumpZero(number, z1, Program.EMPTY_LABEL, destinationLabel));
            expandedInstructions.add(new Decrease(number, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        }
        expandedInstructions.add(new JumpNotZero(number, z1, Program.EMPTY_LABEL, destinationLabel));
        expandedInstructions.add(new Neutral(number, z1, L1, Program.EMPTY_LABEL)); // z1 should be y
        return expandedInstructions;
    }
}
