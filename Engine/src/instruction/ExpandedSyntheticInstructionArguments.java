package instruction;

import instruction.component.Label;
import instruction.component.Variable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExpandedSyntheticInstructionArguments {
    private final Set<Variable> variables;
    private final Map<Label,Instruction> labels;
    private final List<Instruction> instructions;

    public ExpandedSyntheticInstructionArguments(Set<Variable> variables, Map<Label, Instruction> labels, List<Instruction> instructions) {
        this.instructions = instructions;
        this.variables = variables;
        this.labels = labels;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public Set<Variable> getVariables() {
        return variables;
    }

    public Map<Label, Instruction> getLabels() {
        return labels;
    }
}

