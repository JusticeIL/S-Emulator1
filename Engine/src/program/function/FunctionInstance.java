package program.function;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FunctionInstance implements FunctionArgument {

    private final Function function;
    private final List<FunctionArgument> arguments;
    private Integer value = null;

    public FunctionInstance(Function function, List<FunctionArgument> arguments) {
        this.function = function;
        this.arguments = new ArrayList<FunctionArgument>(arguments);
    }

    public List<FunctionArgument> getArguments() {
        return arguments;
    }

    public Function getFunction() {
        return function;
    }

    @Override
    public int getValue() {
        return function.execute(arguments);
    }

    @Override
    public String getName() {
        return "(" + function.getUserString() + (arguments.isEmpty() ? "" : "," + arguments.stream().map(FunctionArgument::getName).collect(Collectors.joining(","))) + ")";
    }

    public ExpandedSyntheticInstructionArguments open(LabelFactory labelFactory, VariableFactory variableFactory, Map<Label, Label> labelTransitionsOldToNew, Map<String, Variable> variableTransitionsOldToNew) {
        return function.open(labelFactory, variableFactory, labelTransitionsOldToNew, variableTransitionsOldToNew);
    }
}
