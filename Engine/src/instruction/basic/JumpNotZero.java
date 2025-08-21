package instruction.basic;

import instruction.BasicInstruction;
import program.Program;
import instruction.component.Label;
import instruction.component.Variable;

public class JumpNotZero extends BasicInstruction {

    static private final int CYCLES = 2;

    public JumpNotZero(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = "IF " + variable.getName() + "!=0" + " GOTO " + destinationLabel.getLabelName();
    }

    @Override
    public Label execute() {
        if (variable.getValue() != 0) { // Case: jump condition is met
            return destinationLabel;
        }
        else
            return Program.EMPTY_LABEL;
    }
}