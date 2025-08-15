package Engine;

import Engine.XMLandJaxB.SInstruction;

public class Increase extends BasicInstruction {

    static private final int CYCLES = 1;

    public Increase(SInstruction sInstruction, int num, Variable variable, Label label, Label destinationLabel) {
        super(sInstruction, num, variable, CYCLES,label, destinationLabel);
        command = variable.getName() + " <- " + variable.getName() + " + 1";
    }

    @Override
    public Label execute() {
        int tmp = variable.getValue();
        tmp++;
        variable.setValue(tmp);
        return destinationLabel;
    }
}
