package instruction.basic;

import instruction.BasicInstruction;
import instruction.Instruction;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;

public class Decrease extends BasicInstruction {

    static private final int CYCLES = 1;

    public Decrease(int num, Variable variable, Label label, Label destinationLabel, LabelFactory labelFactory, VariableFactory variableFactory) {
        super(num, variable, CYCLES,label, destinationLabel, labelFactory, variableFactory);
        command = variable.getName() + " <- " + variable.getName() + " - 1";
    }

    public Decrease(int num, Variable variable, Label label, Label destinationLabel, Instruction parentInstruction, LabelFactory labelFactory, VariableFactory variableFactory) {
        super(num, variable, CYCLES,label, destinationLabel, parentInstruction, labelFactory, variableFactory);
        command = variable.getName() + " <- " + variable.getName() + " - 1";
    }

    @Override
    protected BasicInstruction createCopy() {
        return new Decrease(this.number, this.variable, this.label, this.destinationLabel, this.parentInstruction, this.labelFactory, this.variableFactory);
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
