package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Decrease;
import instruction.basic.Increase;
import instruction.basic.JumpNotZero;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.*;

public class Assignment extends SyntheticInstruction {

    static private final int CYCLES = 4;
    Variable argumentVariable;

    public Assignment(int num, Variable assignedVariable, Label label, Label destinationLabel, Variable argumentVariable) {
        super(num, assignedVariable, CYCLES, label, destinationLabel);
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
    public ExpandedSyntheticInstructionArguments expand() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label,Instruction> expandedLabels = new HashMap<>();

        Label L1 = new Label();
        Label L2 = new Label();
        Label L3 = new Label();
        Variable z1 = new Variable();
        Instruction L1Instruction = new Decrease(4, argumentVariable, L1, Program.EMPTY_LABEL);
        Instruction L2Instruction = new Decrease(7, z1, L2, Program.EMPTY_LABEL);
        Instruction L3Instruction = new Neutral(11, variable, L3, Program.EMPTY_LABEL);
        expandedLabels.put(L1, L1Instruction);
        expandedLabels.put(L2, L2Instruction);
        expandedLabels.put(L3, L3Instruction);

        expandedInstructions.add(new ZeroVariable(1, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        expandedInstructions.add(new JumpNotZero(2, argumentVariable, Program.EMPTY_LABEL, L1));
        expandedInstructions.add(new GoToLabel(3, variable, Program.EMPTY_LABEL, L3));
        expandedInstructions.add(L1Instruction);
        expandedInstructions.add(new Increase(5, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        expandedInstructions.add(new JumpNotZero(6, argumentVariable, Program.EMPTY_LABEL, L1));
        expandedInstructions.add(L2Instruction);
        expandedInstructions.add(new Increase(8, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        expandedInstructions.add(new Increase(9, argumentVariable, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        expandedInstructions.add(new JumpNotZero(10, z1, Program.EMPTY_LABEL, L2));
        expandedInstructions.add(L3Instruction);
        isExpanded = true;
        this.expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels, expandedInstructions);
        return this.expandedInstruction;    }
}
