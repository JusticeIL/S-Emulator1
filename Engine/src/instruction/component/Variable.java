package instruction.component;

import program.function.FunctionArgument;

import java.io.Serializable;
import java.util.Objects;

public class Variable implements Serializable, FunctionArgument {

    protected final String name;
    protected int value;

    public Variable(int highestUnusedZId) {
        this.value = 0;
        this.name = "z"+ highestUnusedZId;
    }

    public Variable(String name, int value) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}