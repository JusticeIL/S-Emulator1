package instruction.synthetic;

import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Decrease;
import instruction.basic.Increase;
import instruction.basic.JumpNotZero;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.ArrayList;
import java.util.List;

public class ConstantAssignment extends SyntheticInstruction {

    static private final int CYCLES = 2;
    private final int constValue;

    public ConstantAssignment(int num, Variable variable, Label label, Label destinationLabel, int constValue) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = variable.getName() + " <- " + constValue;
        this.constValue = constValue;
    }

    @Override
    public Label execute() {
        variable.setValue(constValue);
        return destinationLabel;
    }

    @Override
    public List<Instruction> expand() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Variable z1 = new Variable();
        expandedInstructions.add(new ZeroVariable(number, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        for (int i = 0 ; i<constValue; i++) { // It looks disgusting in lambda
            expandedInstructions.add(new Increase(number, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        }
        return expandedInstructions;
    }
}
