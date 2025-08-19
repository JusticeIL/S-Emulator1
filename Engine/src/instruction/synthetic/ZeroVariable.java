package instruction.synthetic;

import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Decrease;
import instruction.basic.Increase;
import instruction.basic.JumpNotZero;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.ArrayList;
import java.util.List;

public class ZeroVariable extends SyntheticInstruction {

    static private final int CYCLES = 1;

    public ZeroVariable(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = variable.getName() + " <- " + "0";
    }

    @Override
    public Label execute() {
        variable.setValue(0);
        return destinationLabel;
    }

    @Override
    public List<Instruction> expand() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        expandedInstructions.add(new Decrease(number, variable, label, Program.EMPTY_LABEL));
        expandedInstructions.add(new JumpNotZero(number, variable, Program.EMPTY_LABEL, label));
        return expandedInstructions;
    }
}
