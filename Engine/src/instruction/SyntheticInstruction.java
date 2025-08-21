package instruction;

import instruction.component.Label;
import instruction.component.Variable;
import program.InstructionExecutioner;

import java.util.*;

abstract public class SyntheticInstruction extends Instruction {
    protected Variable variable;
    protected boolean isExpanded;
    protected ExpandedSyntheticInstructionArguments expandedInstruction;

    public SyntheticInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel) {
        super(num, cycles ,label, destinationLabel,InstructionType.S);
        this.variable = variable;
        this.isExpanded = false;
    }


    public ExpandedSyntheticInstructionArguments expand() {
        if(isExpanded) {
            List<Instruction> expandedInstructions = new ArrayList<>();
            Set<Variable> expandedVariables = new HashSet<>();
            Map<Label, Instruction> expandedLabels = new HashMap<>();
            expandedInstruction.getInstructions().stream().map(Instruction::expand).forEach(
                args -> {
                    expandedInstructions.addAll(args.getInstructions());
                    expandedVariables.addAll(args.getVariables());
                    expandedLabels.putAll(args.getLabels());
                }
            );;
            return new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels,expandedInstructions);
        }
        return expandSyntheticInstruction();
    }

    protected abstract ExpandedSyntheticInstructionArguments expandSyntheticInstruction();

    @Override
    public Label execute() {
        if(isExpanded){
            return InstructionExecutioner.executeInstructions(expandedInstruction.getInstructions(), expandedInstruction.getLabels());
        }
        return executeUnExpandedInstruction();
    }

    protected abstract Label executeUnExpandedInstruction();
}