package instruction.synthetic.quoting;

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


    public FunctionInvokingInstruction(int num, Variable variable, Label label,Label destinationLabel, Function function, List<FunctionArgument> arguments) {
        super(num, variable, function.getProgramCycles(), label, destinationLabel);
        this.function = new FunctionInstance(function, arguments);
    }

    @Override
    public List<FunctionArgument> getInnerFunctionVariables() {
        List<FunctionArgument> args = new ArrayList<>();
        for (FunctionArgument argument : this.function.getArguments()) {
            args.addAll(argument.getInnerArgument());
        }
        return args;
    }

    @Override
    public int getCycles() {
        return super.getCycles()+ function.getFunction().getProgramCycles();
    }
}
