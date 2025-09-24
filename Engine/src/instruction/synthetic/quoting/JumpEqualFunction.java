package instruction.synthetic.quoting;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;
import instruction.synthetic.JumpEqualVariable;
import program.Program;
import program.function.Function;
import program.function.FunctionArgument;

import java.util.*;
import java.util.stream.Collectors;

public class JumpEqualFunction extends FunctionInvokingInstruction {

    private ExpandedSyntheticInstructionArguments expandedInstructions;
    private VariableFactory variableFactory;

    public JumpEqualFunction(int num, Variable variable, Label label,Label destinationLabel, Function function, List<FunctionArgument> arguments, VariableFactory variableFactory) {
        super(num, variable, label, destinationLabel, function, arguments);
        this.variableFactory = variableFactory;
        this.expandedInstructions = createExpandedInstructions();

        String joinedVariableNames = arguments.stream()
                .map(FunctionArgument::getName)
                .collect(Collectors.joining(","));
        command = "IF " + variable.getName() + " = " + "(" + function.getUserString() + (joinedVariableNames.isEmpty() ? "" : "," + joinedVariableNames) + ")"
        + " GOTO " + destinationLabel.getLabelName();

        int quotationExpansionLevel = expandedInstructions.getInstructions().stream()
                .mapToInt(Instruction::getLevel)
                .max()
                .orElse(0);
        super.level = quotationExpansionLevel + 1; // +1 because expansion of this instruction into the functions' instructions
    }

    private ExpandedSyntheticInstructionArguments createExpandedInstructions() {
        List<Instruction> instructions = new ArrayList<>();
        Variable z1 = variableFactory.createZVariable();
        Instruction quotingInstruction = new Quotation(number, z1, label, function.getFunction(), function.getArguments());
        Instruction jumpEqualVariableInstruction = new JumpEqualVariable(number,variable,Program.EMPTY_LABEL,destinationLabel,z1);

        instructions.add(quotingInstruction);
        instructions.add(jumpEqualVariableInstruction);

        ExpandedSyntheticInstructionArguments expandedInstruction = new ExpandedSyntheticInstructionArguments();
        expandedInstruction.getInstructions().addAll(instructions);
        expandedInstruction.getVariables().add(z1);

        expandedInstruction.getInstructions().forEach(instruction -> {instruction.setParentInstruction(this);});

        return expandedInstruction;
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        if(function.getValue() == variable.getValue()){
            return destinationLabel;
        }
        return Program.EMPTY_LABEL;
    }

    @Override
    public Instruction duplicate(Variable newVariable, Variable newArgumentVariable, Label newLabel, Label newDestinationLabel) {
        return new JumpEqualFunction(number, newVariable, newLabel, destinationLabel, function.getFunction(), function.getArguments(), variableFactory);
    }

    @Override
    protected ExpandedSyntheticInstructionArguments expandSyntheticInstruction(LabelFactory labelFactory, VariableFactory variableFactory) {
        return expandedInstructions;
    }
}