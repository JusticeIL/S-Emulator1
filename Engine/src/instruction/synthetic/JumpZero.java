package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.JumpNotZero;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.*;

public class JumpZero extends SyntheticInstruction {

    static private final int CYCLES = 2;

    public JumpZero(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = "IF " + variable.getName() + " =0" + " GOTO " + destinationLabel.getLabelName();
        super.level = 2;
    }

    @Override
    public Label execute() {
        if (variable.getValue() == 0) {
            return destinationLabel;
        } else {
            return Program.EMPTY_LABEL; // No jump, handle later
        }
    }

    @Override
    public ExpandedSyntheticInstructionArguments expand() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label, Instruction> expandedLabels = new HashMap<>();
        Label L1 = new Label();
        Instruction L1Instruction = new Neutral(number, variable, L1, Program.EMPTY_LABEL);
        expandedLabels.put(L1, L1Instruction);
        expandedInstructions.add(new JumpNotZero(number, variable, Program.EMPTY_LABEL, L1));
        expandedInstructions.add(new GoToLabel(number, variable, Program.EMPTY_LABEL, destinationLabel));
        expandedInstructions.add(L1Instruction); // variable should be y
        isExpanded = true;
        this.expandedInstructions = expandedInstructions;
        return new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels);
    }
}
