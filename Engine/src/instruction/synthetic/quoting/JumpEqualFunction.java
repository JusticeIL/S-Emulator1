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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JumpEqualFunction extends FunctionInvokingInstruction {

    public JumpEqualFunction(int num, Variable variable, Label label,Label destinationLabel, Function function, List<FunctionArgument> arguments) {
        super(num, variable, label, destinationLabel, function, arguments);

        String joinedVariableNames = arguments.stream()
                .map(FunctionArgument::getName)
                .collect(Collectors.joining(","));
        command = "IF " + variable.getName() + " = " + "(" + function.getUserString() + (joinedVariableNames.isEmpty() ? "" : "," + joinedVariableNames) + ")"
        + " GOTO " + destinationLabel.getLabelName();

        int maxArgExpansion = arguments.stream()
                .mapToInt(FunctionArgument::getMaxExpansionLevel)
                .max()
                .orElse(0);
        super.level = Math.max(function.getMaxProgramLevel(), maxArgExpansion) + 1; // +1 because expansion of this instruction into the functions' instructions
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
        return new JumpEqualFunction(number, newVariable, newLabel, destinationLabel, function.getFunction(), function.getArguments());
    }

    @Override
    protected ExpandedSyntheticInstructionArguments expandSyntheticInstruction(LabelFactory labelFactory, VariableFactory variableFactory) {
        Map<Label,Label> LabelTransitionsOldToNew = new HashMap<>();
        Map<String,Variable> VariableTransitionsOldToNew = new HashMap<>();
        Variable functionYValue = variableFactory.createZVariable();
        Label exitLabelForFunction = Program.EMPTY_LABEL;
        if(function.getFunction().getLabelNames().contains(Program.EXIT_LABEL)) {
            exitLabelForFunction = labelFactory.createLabel();
            LabelTransitionsOldToNew.put(Program.EXIT_LABEL, exitLabelForFunction);
        }

        VariableTransitionsOldToNew.put("y",functionYValue);
        Instruction jumpEqualVariableInstruction = new JumpEqualVariable(number,variable,destinationLabel,Program.EMPTY_LABEL,functionYValue);
        Instruction originalLabelPlaceHolder = new Neutral(number,variable,label,Program.EMPTY_LABEL);
        ExpandedSyntheticInstructionArguments expandedInstruction = function.open(labelFactory, variableFactory, LabelTransitionsOldToNew, VariableTransitionsOldToNew,this);

        expandedInstruction.getInstructions().addFirst(originalLabelPlaceHolder);
        expandedInstruction.getInstructions().addLast(jumpEqualVariableInstruction);
        expandedInstruction.getInstructions().forEach(instruction -> {instruction.setParentInstruction(this);});

        return expandedInstruction;
    }
}