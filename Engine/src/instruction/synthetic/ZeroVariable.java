package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Decrease;
import instruction.basic.JumpNotZero;
import instruction.component.Label;
import instruction.component.Variable;
import program.Program;

import java.util.*;

public class ZeroVariable extends SyntheticInstruction {

    static private final int CYCLES = 1;


    public ZeroVariable(int num, Variable variable, Label label, Label destinationLabel) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = variable.getName() + " <- " + "0";
        level = 1;
    }

    @Override
    public Label execute() {
        variable.setValue(0);
        return destinationLabel;
    }

    @Override
    public ExpandedSyntheticInstructionArguments expand() {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label,Instruction> expandedLabels = new HashMap<>();
        Label L1 = new Label();
        Instruction L1Instruction = new Decrease(number, variable, L1, Program.EMPTY_LABEL);

        expandedLabels.put(L1,L1Instruction);
        expandedInstructions.add(L1Instruction);
        expandedInstructions.add(new JumpNotZero(number, variable, Program.EMPTY_LABEL, L1));
        this.expandedInstructions = expandedInstructions;
        isExpanded = true;
        return new ExpandedSyntheticInstructionArguments(expandedVariables, expandedLabels) ;
    }
}
