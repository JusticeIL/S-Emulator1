package instruction;

import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;

import java.util.*;

abstract public class BasicInstruction extends Instruction {

    public BasicInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel) {
        super(num, cycles, label, destinationLabel, InstructionType.B, variable);
        architecture = ArchitectureGeneration.I;
    }

    public BasicInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel, Instruction parentInstruction) {
        super(num, cycles, label, destinationLabel, InstructionType.B, variable, parentInstruction);
        architecture = ArchitectureGeneration.I;
    }

    @Override
    public List<String> getExpandedStringRepresentation() {
        // Use ArrayList to ensure mutability
        List<String> result = new ArrayList<>();
        result.add(this.toString());
        return result;
    }

    @Override
    public ExpandedSyntheticInstructionArguments generateExpandedInstructions(LabelFactory labelFactory, VariableFactory variableFactory) {
        Set<Variable> newVariables = new HashSet<>();
        Map<Label,Instruction> newLabels = new HashMap<>();
        List<Instruction> newInstructions = new ArrayList<>();
        Instruction newInstruction = this.createCopy();
        newInstructions.add(newInstruction);
        newLabels.put(this.label, newInstruction);
        return new ExpandedSyntheticInstructionArguments(newVariables, newLabels,newInstructions);
    }

    abstract protected BasicInstruction createCopy();
}