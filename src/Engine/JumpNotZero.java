package Engine;

import Engine.XMLandJaxB.SInstruction;

public class JumpNotZero extends BasicInstruction {

    public JumpNotZero(SInstruction sInstruction, int num, Variable variable) {
        super(sInstruction, num, variable);
        command = "IF " + variable.getName() + "!=0" + " GOTO " + destinationLabel.getLabelName();
    }

    @Override
    public Label execute() {
        return destinationLabel;
    }
}