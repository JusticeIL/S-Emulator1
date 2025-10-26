package dto;

import java.util.Set;

public class ExecutionPayload {
    private String architecture;
    private Set<VariableDTO> arguments;

    public String getArchitecture() {
        return architecture;
    }

    public Set<VariableDTO> getArguments() {
        return arguments;
    }

    public ExecutionPayload(Set<VariableDTO> arguments,String architecture) {
        this.architecture = architecture;
        this.arguments = arguments;
    }
}
