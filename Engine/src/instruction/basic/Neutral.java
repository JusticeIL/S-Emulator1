package instruction.basic;

import instruction.BasicInstruction;
import instruction.Instruction;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;

public class Neutral extends BasicInstruction {

    static private final int CYCLES = 0;

    public Neutral(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = variable.getName() + " <- " + variable.getName();
    }

    public Neutral(int num, Variable variable, Label label, Label destinationLabel, Instruction parentInstruction) {
        super(num, variable, CYCLES, label, destinationLabel, parentInstruction);
        command = variable.getName() + " <- " + variable.getName();
    }

    @Override
    protected BasicInstruction createCopy() {
        return new Neutral(number, this.variable, this.label, this.destinationLabel, this.parentInstruction);
    }

    @Override
    public Label execute() {
        return destinationLabel;
    }
}