package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Decrease;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.*;

public class JumpEqualVariable extends SyntheticInstruction {

    static private final int CYCLES = 2;
    private final Variable argumentVariable;

    public JumpEqualVariable(int num, Variable variable, Label label, Label destinationLabel, Variable argumentVariable) {
        super(num, variable, CYCLES, label, destinationLabel);
        this.argumentVariable = argumentVariable;
        command = command = "IF " + variable.getName() + " = " + argumentVariable.getName() + " GOTO " + destinationLabel.getLabelName();;
        super.level = 3; // Implement
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        if (variable.getValue() == argumentVariable.getValue()) {
            return destinationLabel;
        } else {
            return Program.EMPTY_LABEL; // No jump, handle later
        }
    }

    @Override
    public ExpandedSyntheticInstructionArguments expand() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label,Instruction> expandedLabels = new HashMap<>();
        int instructionNumber = 1;
        Label L1 = new Label();
        Label L2 = new Label();
        Label L3 = new Label();
        Variable z1 = new Variable();
        Variable z2 = new Variable();
        expandedVariables.add(z1);
        expandedVariables.add(z2);

        expandedInstructions.add(new Assignment(instructionNumber++, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL, variable));
        expandedInstructions.add(new Assignment(instructionNumber++, z2, Program.EMPTY_LABEL, Program.EMPTY_LABEL, argumentVariable));
        Instruction L2Instruction = new JumpZero(instructionNumber++, z1, L2, L3);
        expandedLabels.put(L2, L2Instruction);
        expandedInstructions.add(L2Instruction);
        expandedInstructions.add(new JumpZero(instructionNumber++, z1, Program.EMPTY_LABEL, L1));
        expandedInstructions.add(new Decrease(instructionNumber++, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        expandedInstructions.add(new Decrease(instructionNumber++, z2, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        expandedInstructions.add(new GoToLabel(instructionNumber++, variable, Program.EMPTY_LABEL, L2));
        Instruction L3Instruction = new JumpZero(instructionNumber++, z2, L3, destinationLabel);
        expandedLabels.put(L3, L3Instruction);
        expandedInstructions.add(L3Instruction);
        Instruction L1Instruction = new Neutral(instructionNumber++, z1, L1, Program.EMPTY_LABEL);
        expandedLabels.put(L1, L1Instruction);
        expandedInstructions.add(L1Instruction); // z1 should be y

        isExpanded = true;
        this.expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables, expandedLabels, expandedInstructions);
        return this.expandedInstruction;
    }
}
