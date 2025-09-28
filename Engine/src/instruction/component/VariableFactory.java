package instruction.component;

import java.io.Serializable;

public class VariableFactory implements Serializable {

    private int highestUnusedZId;

    public VariableFactory() {
        this.highestUnusedZId = 1;
    }

    public Variable generateVariable(String name, int value) {
        Variable var;
        if (name.contains("z")) {
            var = createZVariable();
        }
        else {
            var = new Variable(name, value);
        }
        return var;
    }

    public Variable createZVariable() {
        return new Variable(highestUnusedZId++);
    }
}