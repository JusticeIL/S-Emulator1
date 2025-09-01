package program.data;

import instruction.component.Variable;

public class VariableDTO {
    private final String name;
    private final int value;

    public VariableDTO(Variable variable) {
        this.name = variable.getName();
        this.value = variable.getValue();
    }

    public String getName() {
        return name;
    }

    public String getStringRepresentationForConsole() {
        return name + " = " + value;
    }

    public int getValue() {
        return value;
    }
}
