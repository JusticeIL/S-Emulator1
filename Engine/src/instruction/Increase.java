package instruction;

import instruction.component.Label;
import instruction.component.Variable;

public class Increase extends BasicInstruction {

    static private final int CYCLES = 1;

    public Increase(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES,label, destinationLabel);
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
