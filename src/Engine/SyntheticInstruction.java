package Engine;

import Engine.XMLandJaxB.SInstruction;

import java.util.List;

abstract public class SyntheticInstruction extends Instruction {

    List<Variable> variables;
    List<Instruction> instructions;

    public SyntheticInstruction(SInstruction sInstruction, int num) {
        super(sInstruction, num);
    }

    abstract public List<Instruction> extend(); //Implement
}