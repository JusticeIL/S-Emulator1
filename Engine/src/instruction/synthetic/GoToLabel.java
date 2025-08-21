package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Increase;
import instruction.basic.JumpNotZero;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.*;

public class GoToLabel extends SyntheticInstruction {

    static private final int CYCLES = 1;

    public GoToLabel(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = "GOTO " + destinationLabel.getLabelName();
        super.level = 1; // Implement
    }

    @Override
    public Label execute() {
        return destinationLabel;
    }

    @Override
    public ExpandedSyntheticInstructionArguments expand() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label,Instruction> expandedLabels = new HashMap<>();
        Variable z1 = new Variable();
        expandedVariables.add(z1);
        expandedInstructions.add(new Increase(number, z1,  label, Program.EMPTY_LABEL));
        expandedInstructions.add(new JumpNotZero(number, z1, Program.EMPTY_LABEL, destinationLabel));
        isExpanded = true;
        this.expandedInstructions = expandedInstructions;
        return new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels);
    }
}
