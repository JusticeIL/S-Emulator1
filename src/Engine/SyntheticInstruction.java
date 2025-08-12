package Engine;

import java.util.List;

abstract public class SyntheticInstruction extends Instruction {

    List<Variable> variables;

    abstract public List<Instruction> extend(); //Implement
}