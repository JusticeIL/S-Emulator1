package instruction.basic;

import instruction.BasicInstruction;
import instruction.component.Label;
import instruction.component.Variable;

public class Neutral extends BasicInstruction {

    static private final int CYCLES = 0;

    public Neutral(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = variable.getName() + " <- " + variable.getName();
    }

    @Override
    public Label execute() {
        return destinationLabel;
    }
}