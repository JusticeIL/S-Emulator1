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
    }

    @Override
    public Label execute() {
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
        Instruction L1Instruction = new Decrease(number, argumentVariable, L1, Program.EMPTY_LABEL);
        Instruction L2Instruction = new Decrease(number, z1, L2, Program.EMPTY_LABEL);
        Instruction L3Instruction = new Neutral(number, variable, L3, Program.EMPTY_LABEL);
        expandedLabels.put(L1, L1Instruction);
        expandedLabels.put(L2, L2Instruction);
        expandedLabels.put(L3, L3Instruction);

        expandedInstructions.add(new ZeroVariable(number, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        expandedInstructions.add(new JumpNotZero(number, argumentVariable, Program.EMPTY_LABEL, L1));
        expandedInstructions.add(new GoToLabel(number, variable, Program.EMPTY_LABEL, L3));
        expandedInstructions.add(L1Instruction);
        expandedInstructions.add(new Increase(number, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        expandedInstructions.add(new JumpNotZero(number, argumentVariable, Program.EMPTY_LABEL, L1));
        expandedInstructions.add(L2Instruction);
        expandedInstructions.add(new Increase(number, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        expandedInstructions.add(new Increase(number, argumentVariable, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        expandedInstructions.add(new JumpNotZero(number, z1, Program.EMPTY_LABEL, L2));
        expandedInstructions.add(L3Instruction);
        isExpanded = true;
        this.expandedInstructions = expandedInstructions;
        return new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels);
    }
}
