package instruction;

import instruction.component.Label;
import instruction.component.Variable;

import java.io.Serializable;
import java.util.*;

public class ExpandedSyntheticInstructionArguments implements Serializable {
    private final Set<Variable> variables;
    private final Map<Label,Instruction> labels;
    private final List<Instruction> instructions;

    public ExpandedSyntheticInstructionArguments(Set<Variable> variables, Map<Label, Instruction> labels, List<Instruction> instructions) {
        this.instructions = instructions;
        this.variables = variables;
        this.labels = labels;
    }

    public ExpandedSyntheticInstructionArguments() {
        this.instructions = new ArrayList<>();
        this.variables = new HashSet<>();
        this.labels = new  HashMap<>();
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

