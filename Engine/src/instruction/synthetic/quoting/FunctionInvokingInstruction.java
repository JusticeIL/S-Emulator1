package instruction.synthetic.quoting;

import instruction.SyntheticInstruction;
import instruction.component.Label;
import instruction.component.Variable;
import program.function.Function;
import program.function.FunctionInstance;
import program.function.FunctionArgument;

import java.util.List;

public abstract class FunctionInvokingInstruction extends SyntheticInstruction {

    protected final FunctionInstance function;


    public FunctionInvokingInstruction(int num, Variable variable, Label label,Label destinationLabel, Function function, List<FunctionArgument> arguments) {
        super(num, variable, function.getProgramCycles(), label, destinationLabel);
        this.function = new FunctionInstance(function, arguments);

        int maxArgExpansion = arguments.stream()
                .mapToInt(FunctionArgument::getMaxExpansionLevel)
                .max()
                .orElse(0);
        super.level = Math.max(function.getMaxProgramLevel(), maxArgExpansion) + 1; // +1 because expansion of this instruction into the functions' instructions
    }
}
