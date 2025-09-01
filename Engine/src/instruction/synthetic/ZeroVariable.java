package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Decrease;
import instruction.basic.JumpNotZero;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;
import program.Program;

import java.util.*;

public class ZeroVariable extends SyntheticInstruction {

    static private final int CYCLES = 1;

    public ZeroVariable(int num, Variable variable, Label label, Label destinationLabel, LabelFactory labelFactory, VariableFactory variableFactory) {
        super(num, variable, CYCLES, label, destinationLabel, labelFactory, variableFactory);
        command = variable.getName() + " <- " + "0";
        level = 1;
    }

    public ZeroVariable(int num, Variable variable, Label label, Label destinationLabel, Instruction parentInstruction, LabelFactory labelFactory, VariableFactory variableFactory) {
        super(num, variable, CYCLES, label, destinationLabel, parentInstruction, labelFactory, variableFactory);
        command = variable.getName() + " <- " + "0";
        level = 1;
    }

    @Override
    public ExpandedSyntheticInstructionArguments expandSyntheticInstruction() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label,Instruction> expandedLabels = new HashMap<>();
        Label L1 = label.equals(Program.EMPTY_LABEL) ? labelFactory.createLabel() : label;

        int instructionNumber = 1;
        Instruction L1Instruction = new Decrease(instructionNumber++, variable, L1, Program.EMPTY_LABEL,this, labelFactory, variableFactory);
        expandedLabels.put(L1, L1Instruction);
        expandedInstructions.add(L1Instruction);
        expandedInstructions.add(new JumpNotZero(instructionNumber, variable, Program.EMPTY_LABEL, L1,this, labelFactory, variableFactory));
        expandedLabels.put(label, expandedInstructions.getFirst());
        isExpanded = true;
        this.expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables, expandedLabels, expandedInstructions);
        return this.expandedInstruction;
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        variable.setValue(0);
        return destinationLabel;
    }
}