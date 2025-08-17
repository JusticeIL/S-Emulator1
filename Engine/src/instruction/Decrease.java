package instruction;

import instruction.component.Label;
import instruction.component.Variable;

public class Decrease extends BasicInstruction {

    static private final int CYCLES = 1;

    public Decrease(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES,label, destinationLabel);
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
