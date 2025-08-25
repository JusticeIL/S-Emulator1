package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Increase;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.*;
import java.util.stream.IntStream;

public class ConstantAssignment extends SyntheticInstruction {

    static private final int CYCLES = 2;
    private final int constValue;

    public ConstantAssignment(int num, Variable variable, Label label, Label destinationLabel, int constValue) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = variable.getName() + " <- " + constValue;
        this.constValue = constValue;
        super.level = 2; // Implement
    }

    public ConstantAssignment(int num, Variable variable, Label label, Label destinationLabel, int constValue, Instruction parentInstruction) {
        super(num, variable, CYCLES, label, destinationLabel, parentInstruction);
        command = variable.getName() + " <- " + constValue;
        this.constValue = constValue;
        super.level = 2; // Implement
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        variable.setValue(constValue);
        return destinationLabel;
    }

    @Override
    public ExpandedSyntheticInstructionArguments expandSyntheticInstruction() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label,Instruction> expandedLabels = new HashMap<>();
        Variable z1 = new Variable();
        expandedVariables.add(z1);
        expandedInstructions.add(new ZeroVariable(1, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL, this));
        IntStream.range(0, constValue).forEach(i -> { // It looks disgusting in lambda
            expandedInstructions.add(new Increase(i+2, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL,this));
        });

        isExpanded = true;
        this.expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels, expandedInstructions);
        return this.expandedInstruction;

    }
}
