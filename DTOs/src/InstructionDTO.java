import instruction.Instruction;

import java.util.HashSet;
import java.util.Set;

public class InstructionDTO {

    private final int id;
    private final String instruction;
    private final String cycles;
    private final VariableDTO variable;
    private final VariableDTO argumentVariable;
    private final LabelDTO label;
    private final LabelDTO destinationLabel;
    private final String fullExpandedStringRepresentation;
    private final String type;
    private final InstructionDTO parentInstruction;
    private final Set<VariableDTO> innerFunctionVariables;

    public InstructionDTO(Instruction instruction) {
        this.id = instruction.getNumber();
        this.instruction = instruction.getCommand();
        this.cycles = instruction.getCyclesStringRepresentation();
        this.label = new LabelDTO(instruction.getLabel().getLabelName());
        this.destinationLabel = new LabelDTO(instruction.getDestinationLabel().getLabelName());
        this.variable = new VariableDTO(instruction.getVariable());
        this.argumentVariable = new VariableDTO(instruction.getArgumentVariable());
        this.fullExpandedStringRepresentation = instruction.toString();
        this.type = instruction.getInstructionType().toString();
        this.innerFunctionVariables = new HashSet<VariableDTO>();

        innerFunctionVariables.addAll(instruction.getInnerFunctionVariables().stream()
                .map(VariableDTO::new)
                .toList()
        );

        if (instruction.getParentInstruction() != null) {
            this.parentInstruction = new InstructionDTO(instruction.getParentInstruction());
        }
        else {
            this.parentInstruction = null;
        }
    }

    public VariableDTO getArgumentVariable() {
        return argumentVariable;
    }

    public String getFullExpandedStringRepresentation() {
        return fullExpandedStringRepresentation;
    }

    public String getType() {
        return type;
    }

    public InstructionDTO getParentInstruction() {
        return parentInstruction;
    }

    public Set<VariableDTO> getInnerFunctionVariables() {
        return innerFunctionVariables;
    }

    public int getId() {
        return id;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getCycles() {
        return cycles;
    }

    public VariableDTO getVariable() {
        return variable;
    }

    public String getLabelName() {
        return label.getName();
    }

    public LabelDTO getLabel() {
        return label;
    }

    public LabelDTO getDestinationLabel() {
        return destinationLabel;
    }
}