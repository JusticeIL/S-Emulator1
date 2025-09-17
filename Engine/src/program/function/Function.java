package program.function;

import XMLandJaxB.SFunction;
import instruction.component.Variable;
import program.Program;
import program.ProgramExecutioner;
import program.data.VariableDTO;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class Function extends Program {

    private final String userString;

    public Function(SFunction sFunction) throws FileNotFoundException {
        super(sFunction.getSInstructions(), sFunction.getName());
        this.userString = sFunction.getUserString();
    }

    public String getUserString() {
        return userString;
    }

    public int execute(List<Variable> arguments) {
        ProgramExecutioner programExecutioner = new ProgramExecutioner();
        programExecutioner.setProgram(this);
        programExecutioner.executeProgram(arguments.stream().map(VariableDTO::new).collect(Collectors.toSet()));
        return getVariables().stream().filter(var->var.getName().equals("y")).toList().getFirst().getValue();
    }
}
