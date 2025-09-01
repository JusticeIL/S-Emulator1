package instruction.basic;

import instruction.BasicInstruction;
import instruction.Instruction;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;

public class Neutral extends BasicInstruction {

    static private final int CYCLES = 0;

    public Neutral(int num, Variable variable, Label label, Label destinationLabel, LabelFactory labelFactory) {
        super(num, variable, CYCLES, label, destinationLabel, labelFactory);
        command = variable.getName() + " <- " + variable.getName();
    }

    public Neutral(int num, Variable variable, Label label, Label destinationLabel, Instruction parentInstruction, LabelFactory labelFactory) {
        super(num, variable, CYCLES, label, destinationLabel, parentInstruction, labelFactory);
        command = variable.getName() + " <- " + variable.getName();
    }

    @Override
    protected BasicInstruction createCopy() {
        return new Neutral(number, this.variable, this.label, this.destinationLabel, this.parentInstruction, this.labelFactory);
    }

    @Override
    public Label execute() {
        return destinationLabel;
    }
}