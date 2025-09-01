package instruction.synthetic;
import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Decrease;
import instruction.basic.JumpNotZero;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import program.Program;
import java.util.*;

public class JumpEqualConstant extends SyntheticInstruction {

    static private final int CYCLES = 2;
    private final int constValue;

    public JumpEqualConstant(int num, Variable variable, Label label, Label destinationLabel, int constValue, LabelFactory labelFactory) {
        super(num, variable, CYCLES, label, destinationLabel, labelFactory);
        command = "IF " + variable.getName() + " = " + constValue + " GOTO " + destinationLabel.getLabelName();
        this.constValue = constValue;
        super.level = 3;
    }

    public JumpEqualConstant(int num, Variable variable, Label label, Label destinationLabel, int constValue, Instruction parentInstruction, LabelFactory labelFactory) {
        super(num, variable, CYCLES, label, destinationLabel, parentInstruction, labelFactory);
        command = "IF " + variable.getName() + " = " + constValue + " GOTO " + destinationLabel.getLabelName();
        this.constValue = constValue;
        super.level = 3;
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        if (variable.getValue() == constValue) {
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
        Variable z1 = new Variable();
        Label L1 = labelFactory.createLabel();
        int instructionNumber = 1;
        expandedVariables.add(z1);

        expandedInstructions.add(new Assignment(instructionNumber++, z1, label, Program.EMPTY_LABEL, variable, this, labelFactory));
        for (int i = 0; i < constValue; i++) {
            expandedInstructions.add(new JumpZero(instructionNumber++, z1, Program.EMPTY_LABEL, destinationLabel, this, labelFactory));
            expandedInstructions.add(new Decrease(instructionNumber++, z1, Program.EMPTY_LABEL, Program.EMPTY_LABEL, this, labelFactory));
        }
        expandedInstructions.add(new JumpNotZero(instructionNumber++, z1, Program.EMPTY_LABEL, L1, this, labelFactory));
        expandedInstructions.add(new GoToLabel(instructionNumber++, z1, Program.EMPTY_LABEL, destinationLabel, this, labelFactory));
        Instruction L1Instruction = new Neutral(instructionNumber, z1, L1, Program.EMPTY_LABEL, this, labelFactory);
        expandedLabels.put(L1, L1Instruction);
        expandedInstructions.add(L1Instruction);
        isExpanded = true;
        expandedLabels.put(label, expandedInstructions.getFirst());
        this.expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels, expandedInstructions);
        return this.expandedInstruction;
    }
}
