package instruction.synthetic.quoting;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.basic.Neutral;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;
import instruction.synthetic.Assignment;
import program.Program;
import program.function.Function;
import program.function.FunctionInstance;
import program.function.HasValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FunctionInvokingInstruction extends SyntheticInstruction {

    protected final FunctionInstance function;


    public FunctionInvokingInstruction(int num, Variable variable, Label label,Label destinationLabel, Function function, List<HasValue> arguments) {
        super(num, variable, function.getProgramCycles(), label, destinationLabel);
        this.function = new FunctionInstance(function,arguments);
    }
}
