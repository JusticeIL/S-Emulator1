package instruction.component;

public class Variable {

    protected int value;
    protected final String name;

    public Variable(String name, int value) {
        this.value = value;
        this.name = name;
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
}