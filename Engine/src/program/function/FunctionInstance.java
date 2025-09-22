package program.function;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;
import instruction.synthetic.Assignment;
import instruction.synthetic.quoting.Quotation;
import program.Program;

import java.util.ArrayList;
import java.util.Collection;
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

    @Override
    public Function tryGetFunction() {
        return getFunction();
    }

    @Override
    public List<FunctionArgument> tryGetFunctionArguments() {
        return getArguments();
    }

    public ExpandedSyntheticInstructionArguments open(LabelFactory labelFactory, VariableFactory variableFactory, Map<Label, Label> labelTransitionsOldToNew, Map<String, Variable> variableTransitionsOldToNew,Instruction parent) {

        ExpandedSyntheticInstructionArguments openedFunction = function.open(labelFactory, variableFactory, labelTransitionsOldToNew, variableTransitionsOldToNew);
        insertArgumentsIntoOpenFunction(variableTransitionsOldToNew,parent).forEach(openedFunction.getInstructions()::addFirst);
        return openedFunction;
    }

    private List<Instruction> insertArgumentsIntoOpenFunction(Map<String, Variable> variableTransitionsOldToNew,Instruction parent) {
        //for every argument in functionInstance add quote Instruction to insert x var
        List<Instruction> newArgumentSetupQuoteInstructions = new ArrayList<>();


        int argumentIndex = 1;
        for (FunctionArgument argument : arguments) {
            String innerArgumentName = "x"+argumentIndex;
            Variable newVariable = variableTransitionsOldToNew.get(innerArgumentName);
            if(newVariable == null) {
                newVariable = function.getVariables().stream().filter(var->var.getName().equals(innerArgumentName)).findFirst().get();
            }
            Instruction newArgumentSetupInstruction;

            if (argument.getName().contains("(")){//if function instance
                Function argumentFunction = argument.tryGetFunction();
                List<FunctionArgument> argumentFunctionArguments = argument.tryGetFunctionArguments();
                newArgumentSetupInstruction = new Quotation(0,newVariable, Program.EMPTY_LABEL,argumentFunction,argumentFunctionArguments);
            }else{//if variable
                Variable assignmentArgument;
                assignmentArgument = (Variable)argument;
                newArgumentSetupInstruction = new Assignment(0,newVariable,Program.EMPTY_LABEL,Program.EMPTY_LABEL,assignmentArgument);
            }
            newArgumentSetupInstruction.setParentInstruction(parent);
            newArgumentSetupQuoteInstructions.add(newArgumentSetupInstruction);
            argumentIndex++;
        }

        return newArgumentSetupQuoteInstructions;
    }
}
