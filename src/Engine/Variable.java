package Engine;

public class Variable {

    private int value;
    private String name;
    private int id;
    private static int highestUsedID;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}