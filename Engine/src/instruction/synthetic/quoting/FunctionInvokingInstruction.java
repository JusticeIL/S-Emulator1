package instruction.synthetic.quoting;

import dto.ArchitectureGeneration;
import instruction.SyntheticInstruction;
import instruction.component.Label;
import instruction.component.Variable;
import program.function.Function;
import program.function.FunctionInstance;
import program.function.FunctionArgument;

import java.util.ArrayList;
import java.util.List;

public abstract class FunctionInvokingInstruction extends SyntheticInstruction {

    protected final FunctionInstance function;

    public FunctionInvokingInstruction(int num, Variable variable, int cycles, Label label, Label destinationLabel, Function function, List<FunctionArgument> arguments) {
        super(num, variable, cycles, label, destinationLabel);
        this.function = new FunctionInstance(function, arguments);
        architecture = ArchitectureGeneration.IV;
    }

    @Override
    public int getCycles() {
        int baseCycles = super.getCycles();
        int functionTotalCycles = function.getCyclesEvaluation(); // Including arguments' cycles inside

        return baseCycles + functionTotalCycles;
    }

    @Override
    public String getCyclesStringRepresentation() {
        return cycles + "+";
    }

    @Override
    public List<FunctionArgument> getInnerFunctionVariables() {
        List<FunctionArgument> args = new ArrayList<>();
        for (FunctionArgument argument : this.function.getArguments()) {
            args.addAll(argument.getInnerArgument());
        }
        return args;
    }
}