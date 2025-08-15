package Engine;

import Engine.XMLandJaxB.SInstruction;

public class JumpNotZero extends BasicInstruction {

    static private final int CYCLES = 2;

    public JumpNotZero(SInstruction sInstruction, int num, Variable variable) {
        super(sInstruction, num, variable, CYCLES);
        command = "IF " + variable.getName() + "!=0" + " GOTO " + destinationLabel.getLabelName();
    }

    @Override
    public Label execute() {
        if (variable.getValue() != 0) {
            return destinationLabel;
        }
        else
            return null; // No jump, handle later
    }
}