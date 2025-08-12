package Engine;

import Engine.XMLandJaxB.SInstruction;

public class Increase extends BasicInstruction{

    public Increase(SInstruction sInstruction, int num, Variable variable) {
        super(sInstruction, num, variable);
    }

    @Override
    public String execute() {
        int tmp = variable.getValue();
        tmp++;
        variable.setValue(tmp);
        return "";
    }

    @Override
    public String toString() {
        return ("#" + number + " " + "B" + " " + "[" + label + "]" + " " + command + " " + cycles);
    }
}
