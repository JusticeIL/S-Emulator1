package instruction.component;

public class Variable {

    private static int highestUnusedZId = 1;
    protected int value;
    protected final String name;

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