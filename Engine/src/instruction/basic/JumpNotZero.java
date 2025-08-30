package instruction.basic;

import instruction.BasicInstruction;
import instruction.Instruction;
import program.Program;
import instruction.component.Label;
import instruction.component.Variable;

public class JumpNotZero extends BasicInstruction {

    static private final int CYCLES = 2;

    public JumpNotZero(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = "IF " + variable.getName() + " !=0 " + " GOTO " + destinationLabel.getLabelName();
    }

    public JumpNotZero(int num, Variable variable, Label label, Label destinationLabel, Instruction parentInstruction) {
        super(num, variable, CYCLES, label, destinationLabel, parentInstruction);
        command = "IF " + variable.getName() + " !=0 " + " GOTO " + destinationLabel.getLabelName();
    }

    @Override
    protected BasicInstruction createCopy() {
        return new JumpNotZero(number, this.variable, this.label, this.destinationLabel, this.parentInstruction);
    }

    @Override
    public Label execute() {
        if (variable.getValue() != 0) { // Case: jump condition is met
            return destinationLabel;
        }
        else
            return Program.EMPTY_LABEL;
    }
}