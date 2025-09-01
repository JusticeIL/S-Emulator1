package instruction;

import instruction.component.Label;
import instruction.component.Variable;
import java.util.*;

abstract public class SyntheticInstruction extends Instruction {

    protected ExpandedSyntheticInstructionArguments expandedInstruction;
    protected boolean isExpanded;

    public SyntheticInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel) {
        super(num, cycles ,label, destinationLabel,InstructionType.S,variable);

        this.isExpanded = false;
    }

    public SyntheticInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel, Instruction parentInstruction) {
        super(num, cycles ,label, destinationLabel,InstructionType.S,variable, parentInstruction);
        this.isExpanded = false;
    }

    public ExpandedSyntheticInstructionArguments generateExpandedInstructions() {
        if(isExpanded) {
            List<Instruction> expandedInstructions = new ArrayList<>();
            Set<Variable> expandedVariables = new HashSet<>();
            Map<Label, Instruction> expandedLabels = new HashMap<>();

            expandedInstruction.getInstructions().stream().map(Instruction::generateExpandedInstructions).forEach(
                args -> {
                    expandedInstructions.addAll(args.getInstructions());
                    expandedVariables.addAll(args.getVariables());
                    expandedLabels.putAll(args.getLabels());
                    expandedLabels.put(this.label, expandedInstructions.getFirst());
                }
            );
            return new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels,expandedInstructions);
        }
        return expandSyntheticInstruction();
    }

    @Override
    public List<String> getExpandedStringRepresentation() {
        List<String> result = new ArrayList<>();
        if (isExpanded) {
            String prefix = "<<<" + this + " ";
            for (Instruction instr : expandedInstruction.getInstructions()) {
                List<String> subList = instr.getExpandedStringRepresentation();
                subList.replaceAll(s -> s + prefix);
                result.addAll(subList);
            }
        } else {
            result.add(this.toString());
        }
        return result;
    }

    protected abstract ExpandedSyntheticInstructionArguments expandSyntheticInstruction();

    @Override
    public Label execute() {
        return executeUnExpandedInstruction();
    }

    protected abstract Label executeUnExpandedInstruction();
}