package Engine;

import Engine.XMLandJaxB.SInstruction;

public class JumpNotZero extends BasicInstruction {

    static private final int CYCLES = 2;

    public JumpNotZero(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = "IF " + variable.getName() + "!=0" + " GOTO " + destinationLabel.getLabelName();
    }

    @Override
    public Label execute() {
        if (variable.getValue() != 0) {
            return destinationLabel;
        }
        else
            return Program.EMPTY_LABEL; // No jump, handle later
    }
}