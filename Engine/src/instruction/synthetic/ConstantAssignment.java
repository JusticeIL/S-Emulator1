package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Increase;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

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
    public ExpandedSyntheticInstructionArguments expand() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Set<Label> expandedLabels = new HashSet<>();
        Variable z1 = new Variable();
        expandedVariables.add(z1);
        expandedInstructions.add(new ZeroVariable(number, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        IntStream.range(0, constValue).forEach(i -> { // It looks disgusting in lambda
            expandedInstructions.add(new Increase(number, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        });

        isExpanded = true;
        this.expandedInstructions = expandedInstructions;
        return new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels);
    }
}
