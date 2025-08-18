package instruction;

import instruction.component.Label;
import instruction.component.Variable;

import java.util.List;

public class Assignment extends SyntheticInstruction {

    static private final int CYCLES = 4;

    public Assignment(int num, List<Variable> variables, Label label, Label destinationLabel) {
        super(num, variables, CYCLES, label, destinationLabel);
        if (variables.size() != 2) {
            throw new IllegalArgumentException("Assignment Instruction must have exactly two variables"); // Temporal fix
        }
        command = variables.getFirst().getName() + " <- " + variables.get(1).getName();
    }

    @Override
    public Label execute() {
        variables.getFirst().setValue(variables.get(1).getValue());
        return destinationLabel;
    }

    @Override
    public List<Instruction> expand() {
        return List.of();
    }
}
