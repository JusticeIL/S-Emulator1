package instruction.synthetic;

import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.component.Label;
import instruction.component.Variable;

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
        return List.of();
    }
}
