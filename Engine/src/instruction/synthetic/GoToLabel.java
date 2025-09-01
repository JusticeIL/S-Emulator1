package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Increase;
import instruction.basic.JumpNotZero;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import program.Program;

import java.util.*;

public class GoToLabel extends SyntheticInstruction {

    static private final int CYCLES = 1;

    public GoToLabel(int num, Variable variable, Label label, Label destinationLabel, LabelFactory labelFactory) {
        super(num, variable, CYCLES, label, destinationLabel, labelFactory);
        command = "GOTO " + destinationLabel.getLabelName();
        super.level = 1;
    }

    public GoToLabel(int num, Variable variable, Label label, Label destinationLabel, Instruction parentInstruction, LabelFactory labelFactory) {
        super(num, variable, CYCLES, label, destinationLabel, parentInstruction, labelFactory);
        command = "GOTO " + destinationLabel.getLabelName();
        super.level = 1;
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        return destinationLabel;
    }

    @Override
    public ExpandedSyntheticInstructionArguments expandSyntheticInstruction() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label,Instruction> expandedLabels = new HashMap<>();
        Variable z1 = new Variable();
        expandedVariables.add(z1);

        expandedInstructions.add(new Increase(1, z1,  label, Program.EMPTY_LABEL, this, labelFactory));
        expandedInstructions.add(new JumpNotZero(2, z1, Program.EMPTY_LABEL, destinationLabel, this, labelFactory));
        isExpanded = true;
        expandedLabels.put(label, expandedInstructions.getFirst());
        this.expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels, expandedInstructions);
        return this.expandedInstruction;

    }
}