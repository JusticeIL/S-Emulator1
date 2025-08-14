package Engine;

import Engine.XMLandJaxB.SInstruction;

public class Decrease extends BasicInstruction{

    public Decrease(SInstruction sInstruction, int num, Variable variable) {
        super(sInstruction, num, variable);
        command = variable.getName() + " <- " + variable.getName() + " - 1";
    }

    @Override
    public Label execute() {
        int tmp = variable.getValue();
        if (tmp >0) { // Case: can be decreased
            tmp--;
            variable.setValue(tmp);
        }
        return destinationLabel;
    }
}
