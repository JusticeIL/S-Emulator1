package instruction.synthetic;

import instruction.ArchitectureGeneration;
import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Increase;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;
import program.Program;

import java.util.*;
import java.util.stream.IntStream;

public class ConstantAssignment extends SyntheticInstruction {

    private final int constValue;
    static private final int CYCLES = 2;
    
    public ConstantAssignment(int num, Variable variable, Label label, Label destinationLabel, int constValue) {
        super(num, variable, CYCLES, label, destinationLabel);
        command = variable.getName() + " <- " + constValue;
        this.constValue = constValue;
        super.level = 2;
        architecture = ArchitectureGeneration.II;
    }

    public ConstantAssignment(int num, Variable variable, Label label, Label destinationLabel, int constValue, Instruction parentInstruction) {
        super(num, variable, CYCLES, label, destinationLabel, parentInstruction);
        command = variable.getName() + " <- " + constValue;
        this.constValue = constValue;
        super.level = 2;
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        variable.setValue(constValue);
        return destinationLabel;
    }

    @Override
    public ExpandedSyntheticInstructionArguments expandSyntheticInstruction(LabelFactory labelFactory, VariableFactory variableFactory) {
        List<Instruction> expandedInstructions = new ArrayList<>();
        Set<Variable> expandedVariables = new HashSet<>();
        Map<Label,Instruction> expandedLabels = new HashMap<>();

        expandedInstructions.add(new ZeroVariable(1, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL, this));
        IntStream.range(0, constValue).forEach(i ->
                expandedInstructions.add(new Increase(i+2, variable, Program.EMPTY_LABEL, Program.EMPTY_LABEL,this)));

        expandedLabels.put(label, expandedInstructions.getFirst());
        isExpanded = true;
        this.expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables,expandedLabels, expandedInstructions);
        return this.expandedInstruction;
    }

    @Override
    public Instruction duplicate(Variable newVariable, Variable newArgumentVariable, Label newLabel, Label newDestinationLabel) {
        return new ConstantAssignment(number,newVariable,newLabel, newDestinationLabel,constValue);
    }
}