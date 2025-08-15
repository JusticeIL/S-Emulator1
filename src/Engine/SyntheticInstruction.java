package Engine;

import Engine.XMLandJaxB.SInstruction;

import java.util.List;

abstract public class SyntheticInstruction extends Instruction {

    List<Variable> variables;
    protected List<Instruction> instructions;

    public SyntheticInstruction(SInstruction sInstruction, int num, Label label, Label destinationLabel) {

        super(sInstruction, num, 0,label, destinationLabel );
    }

    public List<Instruction> extend() { return instructions; }

    @Override
    public String toString() {
        return ("#" + number + " " + "S" + " " + "[" + label + "]" + " " + command + " " + cycles);
    }
}