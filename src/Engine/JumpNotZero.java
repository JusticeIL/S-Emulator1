package Engine;

import Engine.XMLandJaxB.SInstruction;

public class JumpNotZero extends BasicInstruction {

    public JumpNotZero(SInstruction sInstruction, int num, Variable variable) {
        super(sInstruction, num, variable);
    }

    @Override
    public String execute() {
        return destinationLabel;
    }
}