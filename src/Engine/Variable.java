package Engine;

public class Variable {

    private int value;
    private final String name;
    private final int id;
    private static int highestUsedID;

    public Variable(String name, int value) {
        this.value = value;
        this.name = name;
        this.id = ++highestUsedID;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }
}