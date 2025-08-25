package instruction;

import instruction.component.Label;
import instruction.component.Variable;

import java.util.*;

abstract public class BasicInstruction extends Instruction {

    protected Variable variable;

    public ExpandedSyntheticInstructionArguments generateExpandedInstructions() {
        Set<Variable> newVariables = new HashSet<>();
        Map<Label,Instruction> newLabels = new HashMap<>();
        List<Instruction> newInstructions = new ArrayList<>();
        newInstructions.add(this);

        return new ExpandedSyntheticInstructionArguments(newVariables, newLabels,newInstructions);
    }

    @Override
    public void revertExpansion() {
        // No expansion to revert for basic instructions
    }

    public BasicInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel) {
        super(num, cycles, label, destinationLabel, InstructionType.B);
        this.variable = variable;
    }

    @Override
    public List<String> getExpandedStringRepresentation() {
        // Use ArrayList to ensure mutability
        List<String> result = new ArrayList<>();
        result.add(this.toString());
        return result;
    }
}
