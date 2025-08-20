package instruction;

import instruction.component.Label;
import instruction.component.Variable;

import java.util.Set;

public class ExpandedSyntheticInstructionArguments {
    private final Set<Variable> variables;
    private final Set<Label> labels;

    public ExpandedSyntheticInstructionArguments(Set<Variable> variables, Set<Label> labels) {
        this.variables = variables;
        this.labels = labels;
    }


    public Set<Variable> getVariables() {
        return variables;
    }

    public Set<Label> getLabels() {
        return labels;
    }
}

