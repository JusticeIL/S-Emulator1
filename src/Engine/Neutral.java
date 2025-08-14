package Engine;

import Engine.XMLandJaxB.SInstruction;

public class Neutral extends BasicInstruction {

    public Neutral(SInstruction sInstruction, int num, Variable variable) {
        super(sInstruction, num, variable);
        command = variable.getName() + " <- " + variable.getName();
    }

    @Override
    public Label execute() {
        return destinationLabel;
    }
}