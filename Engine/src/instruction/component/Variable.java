package instruction.component;

import java.io.Serializable;
import java.util.Objects;

public class Variable implements Serializable {

    private static int highestUnusedZId = 1;
    private static int previousHighestUnusedZId = 1;
    protected final String name;
    protected int value;

    public Variable() {
        this.value = 0;
        this.name = "z"+ highestUnusedZId;
        highestUnusedZId++;
    }

    public Variable(String name, int value) {
        this.value = value;
        this.name = name;

        if(name.contains("z")) {
            highestUnusedZId++;
        }
    }

    public String getName() {
        return name;
    }

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

    public static void resetZIdCounter() {
        highestUnusedZId = 1;
    }

    public static void saveHighestUnusedZId() {
        previousHighestUnusedZId = highestUnusedZId;
    }

    public static void loadHighestUnusedZId() {
        highestUnusedZId = previousHighestUnusedZId;
    }
}