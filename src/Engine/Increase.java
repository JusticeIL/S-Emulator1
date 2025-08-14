package Engine;

import Engine.XMLandJaxB.SInstruction;

public class Increase extends BasicInstruction {

    public Increase(SInstruction sInstruction, int num, Variable variable) {
        super(sInstruction, num, variable);
    }

    @Override
    public Label execute() {
        int tmp = variable.getValue();
        tmp++;
        variable.setValue(tmp);
        return destinationLabel;
    }
}
