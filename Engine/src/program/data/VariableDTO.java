package program.data;

import program.function.HasValue;

import java.util.Objects;

public class VariableDTO implements Searchable {
    private final String name;
    private final int value;

    public VariableDTO(HasValue variable) {
        this.name = variable.getName();
        this.value = variable.getValue();
    }

    public VariableDTO(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getStringRepresentationForConsole() {
        return name + " = " + value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VariableDTO that = (VariableDTO) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
