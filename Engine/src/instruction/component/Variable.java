package instruction.component;

import java.io.Serializable;

public class Variable implements Serializable {

    private static int highestUnusedZId = 1;
    private static int previousHighestUnusedZId = 1;
    protected int value;
    protected final String name;

    public Variable(String name, int value) {
        this.value = value;
        this.name = name;

        if(name.contains("z")) {
            highestUnusedZId++;
        }
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

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Variable() {
        this.value = 0;
        this.name = "z"+ highestUnusedZId;
        highestUnusedZId++;
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }
}