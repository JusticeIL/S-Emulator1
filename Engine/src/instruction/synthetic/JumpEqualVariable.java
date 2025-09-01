package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Decrease;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;
import program.Program;

import java.util.*;

public class JumpEqualVariable extends SyntheticInstruction {

    static private final int CYCLES = 2;
    private final Variable argumentVariable;

    public JumpEqualVariable(int num, Variable variable, Label label, Label destinationLabel, Variable argumentVariable, LabelFactory labelFactory, VariableFactory variableFactory) {
        super(num, variable, CYCLES, label, destinationLabel, labelFactory, variableFactory);
        this.argumentVariable = argumentVariable;
        command = "IF " + variable.getName() + " = " + argumentVariable.getName() + " GOTO " + destinationLabel.getLabelName();
        super.level = 3;
    }

    public JumpEqualVariable(int num, Variable variable, Label label, Label destinationLabel, Variable argumentVariable, Instruction parentInstruction, LabelFactory labelFactory, VariableFactory variableFactory) {
        super(num, variable, CYCLES, label, destinationLabel, parentInstruction, labelFactory, variableFactory);
        this.argumentVariable = argumentVariable;
        command = "IF " + variable.getName() + " = " + argumentVariable.getName() + " GOTO " + destinationLabel.getLabelName();
        super.level = 3;
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        if (variable.getValue() == argumentVariable.getValue()) {
            return destinationLabel;
        } else {
            return Program.EMPTY_LABEL;
        }
    }

    @Override
    public ExpandedSyntheticInstructionArguments expandSyntheticInstruction() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label,Instruction> expandedLabels = new HashMap<>();
        int instructionNumber = 1;
        Label L1 = labelFactory.createLabel();
        Label L2 = labelFactory.createLabel();
        Label L3 = labelFactory.createLabel();
        Variable z1 = variableFactory.createZVariable();
        Variable z2 = variableFactory.createZVariable();
        expandedVariables.add(z1);
        expandedVariables.add(z2);

        expandedInstructions.add(new Assignment(instructionNumber++, z1, label, Program.EMPTY_LABEL, variable, this, labelFactory, variableFactory));
        expandedInstructions.add(new Assignment(instructionNumber++, z2, Program.EMPTY_LABEL, Program.EMPTY_LABEL, argumentVariable, this, labelFactory, variableFactory));
        Instruction L2Instruction = new JumpZero(instructionNumber++, z1, L2, L3, this, labelFactory, variableFactory);
        expandedLabels.put(L2, L2Instruction);
        expandedInstructions.add(L2Instruction);
        expandedInstructions.add(new JumpZero(instructionNumber++, z2, Program.EMPTY_LABEL, L1, this, labelFactory, variableFactory));
        expandedInstructions.add(new Decrease(instructionNumber++, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL, this, labelFactory, variableFactory));
        expandedInstructions.add(new Decrease(instructionNumber++, z2, Program.EMPTY_LABEL, Program.EMPTY_LABEL, this, labelFactory, variableFactory));
        expandedInstructions.add(new GoToLabel(instructionNumber++, variable, Program.EMPTY_LABEL, L2, this, labelFactory, variableFactory));
        Instruction L3Instruction = new JumpZero(instructionNumber++, z2, L3, destinationLabel, this, labelFactory, variableFactory);
        expandedLabels.put(L3, L3Instruction);
        expandedInstructions.add(L3Instruction);
        Instruction L1Instruction = new Neutral(instructionNumber, z1, L1, Program.EMPTY_LABEL, this, labelFactory, variableFactory);
        expandedLabels.put(L1, L1Instruction);
        expandedInstructions.add(L1Instruction);
        expandedLabels.put(label, expandedInstructions.getFirst());
        isExpanded = true;
        this.expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables, expandedLabels, expandedInstructions);
        return this.expandedInstruction;
    }
}
