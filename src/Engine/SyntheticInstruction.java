package Engine;

import java.util.List;

abstract public class SyntheticInstruction extends Instruction {

    List<Variable> variables;
    List<Instruction> instructions;

    abstract public List<Instruction> extend(); //Implement
}