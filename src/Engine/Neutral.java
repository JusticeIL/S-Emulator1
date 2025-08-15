package Engine;

import Engine.XMLandJaxB.SInstruction;

public class Neutral extends BasicInstruction {

    static private final int CYCLES = 0;

    public Neutral(SInstruction sInstruction, int num, Variable variable, Label label, Label destinationLabel) {
        super(sInstruction, num, variable, CYCLES, label, destinationLabel);
        command = variable.getName() + " <- " + variable.getName();
    }

    @Override
    public Label execute() {
        return destinationLabel;
    }
}