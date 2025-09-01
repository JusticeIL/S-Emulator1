package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Decrease;
import instruction.basic.Increase;
import instruction.basic.JumpNotZero;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;
import program.Program;

import java.util.*;

public class Assignment extends SyntheticInstruction {

    static private final int CYCLES = 4;
    private final Variable argumentVariable;

    public Assignment(int num, Variable assignedVariable, Label label, Label destinationLabel, Variable argumentVariable, LabelFactory labelFactory, VariableFactory variableFactory) {
        super(num, assignedVariable, CYCLES, label, destinationLabel, labelFactory, variableFactory);
        this.argumentVariable = argumentVariable;
        command = assignedVariable.getName() + " <- " + argumentVariable.getName();
        super.level = 2;
    }

    public Assignment(int num, Variable assignedVariable, Label label, Label destinationLabel, Variable argumentVariable, Instruction parentInstruction, LabelFactory labelFactory, VariableFactory variableFactory) {
        super(num, assignedVariable, CYCLES, label, destinationLabel, parentInstruction, labelFactory, variableFactory);
        this.argumentVariable = argumentVariable;
        command = assignedVariable.getName() + " <- " + argumentVariable.getName();
        super.level = 2;
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        variable.setValue(argumentVariable.getValue());
        return destinationLabel;
    }

    @Override
    public ExpandedSyntheticInstructionArguments expandSyntheticInstruction() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label,Instruction> expandedLabels = new HashMap<>();

        Label L1 = labelFactory.createLabel();
        Label L2 = labelFactory.createLabel();
        Label L3 = labelFactory.createLabel();
        Variable z1 = variableFactory.createZVariable();
        expandedVariables.add(z1);
        Instruction L1Instruction = new Decrease(4, argumentVariable, L1, Program.EMPTY_LABEL,this, labelFactory, variableFactory);
        Instruction L2Instruction = new Decrease(7, z1, L2, Program.EMPTY_LABEL,this, labelFactory, variableFactory);
        Instruction L3Instruction = new Neutral(11, variable, L3, Program.EMPTY_LABEL,this, labelFactory, variableFactory);
        expandedLabels.put(L1, L1Instruction);
        expandedLabels.put(L2, L2Instruction);
        expandedLabels.put(L3, L3Instruction);

        expandedInstructions.add(new ZeroVariable(1, variable, label, Program.EMPTY_LABEL, this, labelFactory, variableFactory));
        expandedInstructions.add(new JumpNotZero(2, argumentVariable, Program.EMPTY_LABEL, L1, this, labelFactory, variableFactory));
        expandedInstructions.add(new GoToLabel(3, variable, Program.EMPTY_LABEL, L3, this, labelFactory, variableFactory));
        expandedInstructions.add(L1Instruction);
        expandedInstructions.add(new Increase(5, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL, this, labelFactory, variableFactory));
        expandedInstructions.add(new JumpNotZero(6, argumentVariable, Program.EMPTY_LABEL, L1, this, labelFactory, variableFactory));
        expandedInstructions.add(L2Instruction);
        expandedInstructions.add(new Increase(8, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL, this, labelFactory, variableFactory));
        expandedInstructions.add(new Increase(9, argumentVariable, Program.EMPTY_LABEL, Program.EMPTY_LABEL, this, labelFactory, variableFactory));
        expandedInstructions.add(new JumpNotZero(10, z1, Program.EMPTY_LABEL, L2, this, labelFactory, variableFactory));
        expandedInstructions.add(L3Instruction);
        isExpanded = true;
        expandedLabels.put(label, expandedInstructions.getFirst());
        this.expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels, expandedInstructions);
        return this.expandedInstruction;
    }
}