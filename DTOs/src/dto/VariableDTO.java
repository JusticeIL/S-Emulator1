package dto;

import program.function.FunctionArgument;

import java.util.Objects;

public class VariableDTO implements Searchable {

    private String name;
    private final int value;

    public VariableDTO(FunctionArgument variable) {
        this.name = variable.getName();
        this.value = variable.getValue();
    }

    public VariableDTO(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
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

    // Deprecated
    public String getStringRepresentationForConsole() {
        return name + " = " + value;
    }
}