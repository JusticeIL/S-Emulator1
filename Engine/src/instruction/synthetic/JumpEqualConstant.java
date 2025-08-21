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
    public Label execute() {
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
        Instruction L1Instruction = new Neutral(number, z1, L1, Program.EMPTY_LABEL);
        expandedLabels.put(L1, L1Instruction);
        expandedVariables.add(z1);

        expandedInstructions.add(new Assignment(number, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL, variable));
        IntStream.range(0, constValue).forEach(i -> {
            expandedInstructions.add(new JumpZero(number, z1, Program.EMPTY_LABEL, destinationLabel));
            expandedInstructions.add(new Decrease(number, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL));
        });
        expandedInstructions.add(new JumpNotZero(number, z1, Program.EMPTY_LABEL, destinationLabel));
        expandedInstructions.add(L1Instruction); // z1 should be y
        isExpanded = true;
        this.expandedInstructions = expandedInstructions;
        return new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels);
    }
}
