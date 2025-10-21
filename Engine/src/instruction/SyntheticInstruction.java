package instruction;

import dto.ArchitectureGeneration;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;

import java.util.*;

abstract public class SyntheticInstruction extends Instruction {

    protected boolean isExpanded;
    protected ExpandedSyntheticInstructionArguments expandedInstruction;

    public SyntheticInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel) {
        super(num, cycles ,label, destinationLabel,InstructionType.S, variable);
        this.isExpanded = false;
        architecture = ArchitectureGeneration.III;
    }

    public SyntheticInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel, Instruction parentInstruction) {
        super(num, cycles ,label, destinationLabel,InstructionType.S, variable, parentInstruction);
        this.isExpanded = false;
        architecture = ArchitectureGeneration.III;
    }

    public ExpandedSyntheticInstructionArguments generateExpandedInstructions(LabelFactory labelFactory, VariableFactory variableFactory) {
        return expandSyntheticInstruction(labelFactory, variableFactory);
    }

    @Override
    public Label execute() {
        return executeUnExpandedInstruction();
    }

    @Override
    public List<String> getExpandedStringRepresentation() {
        List<String> result = new ArrayList<>();
        if (isExpanded) {
            String prefix = DELIMITER + this + " ";
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

    protected abstract ExpandedSyntheticInstructionArguments expandSyntheticInstruction(LabelFactory labelFactory, VariableFactory variableFactory);
    protected abstract Label executeUnExpandedInstruction();
}