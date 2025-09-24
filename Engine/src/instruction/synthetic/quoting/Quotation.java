package instruction.synthetic.quoting;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;
import instruction.synthetic.Assignment;
import program.Program;
import program.function.Function;
import program.function.FunctionArgument;
import java.util.*;
import java.util.stream.Collectors;

public class Quotation extends FunctionInvokingInstruction{

    public Quotation(int num, Variable variable, Label label, Function function, List<FunctionArgument> arguments) {
        super(num, variable, label, Program.EMPTY_LABEL, function, arguments);

        String joinedVariableNames = arguments.stream()
                .map(FunctionArgument::getName)
                .collect(Collectors.joining(","));
        command = variable.getName() + " <- " + "(" + function.getUserString() + (joinedVariableNames.isEmpty() ? "" : "," + joinedVariableNames) + ")";

        int maxArgExpansion = arguments.stream()
                .mapToInt(FunctionArgument::getMaxExpansionLevel)
                .max()
                .orElse(0);
        super.level = Math.max(function.getMaxProgramLevel(), maxArgExpansion) + 1; // +1 because expansion of this instruction into the functions' instructions
    }

    @Override
    protected ExpandedSyntheticInstructionArguments expandSyntheticInstruction(LabelFactory labelFactory, VariableFactory variableFactory) {
        Map<Label,Label> LabelTransitionsOldToNew = new HashMap<>();
        Map<String,Variable> VariableTransitionsOldToNew = new HashMap<>();
        Variable functionAssignmentArgument = variableFactory.createZVariable();
        Label exitLabelForFunction = Program.EMPTY_LABEL;
        if(function.getFunction().getLabelNames().contains(Program.EXIT_LABEL)) {
            exitLabelForFunction = labelFactory.createLabel();
            LabelTransitionsOldToNew.put(Program.EXIT_LABEL, exitLabelForFunction);
        }

        VariableTransitionsOldToNew.put("y",functionAssignmentArgument);
        Instruction assimentInstruction = new Assignment(number, variable, exitLabelForFunction, Program.EMPTY_LABEL, functionAssignmentArgument);
        Instruction originalLabelPlaceHolder = new Neutral(number, variable, label, Program.EMPTY_LABEL);
        ExpandedSyntheticInstructionArguments expandedInstruction = function.open(labelFactory, variableFactory, LabelTransitionsOldToNew, VariableTransitionsOldToNew,this);

        expandedInstruction.getInstructions().addFirst(originalLabelPlaceHolder);
        expandedInstruction.getInstructions().addLast(assimentInstruction);

        if(!exitLabelForFunction.equals(Program.EMPTY_LABEL)) {
            expandedInstruction.getLabels().put(exitLabelForFunction, assimentInstruction);
        }
        expandedInstruction.getLabels().put(label, originalLabelPlaceHolder);

        expandedInstruction.getInstructions().forEach(instruction -> {instruction.setParentInstruction(this);});
        return expandedInstruction;
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        variable.setValue(function.getValue());
        return destinationLabel;
    }

    @Override
    public Instruction duplicate(Variable newVariable, Variable newArgumentVariable, Label newLabel, Label newDestinationLabel) {
        return new Quotation(number, newVariable, newLabel, function.getFunction(), function.getArguments());
    }
}