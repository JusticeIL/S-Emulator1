package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Decrease;
import instruction.basic.JumpNotZero;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.*;
import java.util.stream.IntStream;

public class JumpEqualConstant extends SyntheticInstruction {

    static private final int CYCLES = 2;
    private final int constValue;

    public JumpEqualConstant(int num, Variable variable, Label label, Label destinationLabel, int constValue) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = "IF " + variable.getName() + " = " + constValue + " GOTO " + destinationLabel.getLabelName();
        this.constValue = constValue;
        super.level = 3; // Implement
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        if (variable.getValue() == constValue) {
            return destinationLabel;
        } else {
            return Program.EMPTY_LABEL; // No jump, handle later
        }
    }

    @Override
    public ExpandedSyntheticInstructionArguments expand() { // Waiting for answer from Aviad
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label,Instruction> expandedLabels = new HashMap<>();
        Variable z1 = new Variable();
        Label L1 = new Label();
        int instructionNumber = 1;
        expandedVariables.add(z1);

        expandedInstructions.add(new Assignment(instructionNumber++, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL, variable));
        for (int i = 0; i < constValue; i++) {
            expandedInstructions.add(new JumpZero(instructionNumber++, z1, Program.EMPTY_LABEL, destinationLabel));
            expandedInstructions.add(new Decrease(instructionNumber++, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        }
        expandedInstructions.add(new JumpNotZero(instructionNumber++, z1, Program.EMPTY_LABEL, destinationLabel));
        Instruction L1Instruction = new Neutral(instructionNumber++, z1, L1, Program.EMPTY_LABEL);
        expandedLabels.put(L1, L1Instruction);
        expandedInstructions.add(L1Instruction); // z1 should be y
        isExpanded = true;

        this.expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels, expandedInstructions);
        return this.expandedInstruction;

    }
}
