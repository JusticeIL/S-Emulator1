package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.JumpNotZero;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;
import program.Program;

import java.util.*;

public class JumpZero extends SyntheticInstruction {

    static private final int CYCLES = 2;

    public JumpZero(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = "IF " + variable.getName() + " =0" + " GOTO " + destinationLabel.getLabelName();
        super.level = 2;
    }

    public JumpZero(int num, Variable variable, Label label, Label destinationLabel, Instruction parentInstruction) {
        super(num, variable, CYCLES, label, destinationLabel, parentInstruction);
        command = "IF " + variable.getName() + " =0" + " GOTO " + destinationLabel.getLabelName();
        super.level = 2;
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        if (variable.getValue() == 0) {
            return destinationLabel;
        } else {
            return Program.EMPTY_LABEL;
        }
    }

    @Override
    public ExpandedSyntheticInstructionArguments expandSyntheticInstruction(LabelFactory labelFactory, VariableFactory variableFactory) {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label, Instruction> expandedLabels = new HashMap<>();
        Label L1 = labelFactory.createLabel();

        int instructionNumber = 1;
        expandedInstructions.add(new JumpNotZero(instructionNumber++, variable, label, L1, this));
        expandedInstructions.add(new GoToLabel(instructionNumber++, variable, Program.EMPTY_LABEL, destinationLabel, this));
        Instruction L1Instruction = new Neutral(instructionNumber, variable, L1, Program.EMPTY_LABEL, this);
        expandedLabels.put(L1, L1Instruction);
        expandedInstructions.add(L1Instruction);

        isExpanded = true;
        expandedLabels.put(label, expandedInstructions.getFirst());
        this.expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables, expandedLabels, expandedInstructions);
        return this.expandedInstruction;
    }

    @Override
    public Instruction duplicate(Variable newVariable, Variable newArgumentVariable, Label newLabel, Label newDestinationLabel) {
        return new JumpZero(number,newVariable,newLabel, newDestinationLabel);
    }
}