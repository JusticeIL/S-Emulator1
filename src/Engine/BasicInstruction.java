package Engine;

import java.util.ArrayList;
import java.util.List;

abstract public class BasicInstruction extends Instruction {

    Variable variable;

    public List<Instruction> expand() {
        List<Instruction> res = new ArrayList<>(1);
        res.add(this);

        return res;
    }
}
