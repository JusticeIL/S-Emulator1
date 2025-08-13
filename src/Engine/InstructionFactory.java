package Engine;

import Engine.XMLandJaxB.SInstruction;
import Engine.XMLandJaxB.SInstructionArgument;

import java.util.Map;

public class InstructionFactory {
    Map<String, Variable> variables;
    public Instruction GenerateInstruction(SInstruction sInstr, int instructionListLength) {
        Variable variable = GetVariable(sInstr.getSVariable());
        Instruction instruction;
        switch(sInstr.getName()){
            case("INCREASE"):
                instruction = new Increase(sInstr,instructionListLength, variable);
            case("DECREASE"):
                instruction = new Decrease(sInstr,instructionListLength, variable);
            case("JUMP_NOT_ZERO"):
                instruction = new JumpNotZero(sInstr,instructionListLength, variable);
            case("NEUTRAL"):
                instruction = new Neutral(sInstr,instructionListLength, variable);
            default:
                throw new IllegalArgumentException("Invalid Instruction");
        }


    }

    InstructionFactory(Map<String, Variable> variables) {
        this.variables = variables;
    }

    Variable GetVariable(String variableName) {
        try {
            if (variables.containsKey(variableName)) {
                return variables.get(variableName);
            }else{
                Variable variable = new Variable(variableName,0);
                variables.put(variableName, variable);
                return variable;
            }
            } catch (NumberFormatException e) {
            System.out.println("HARA AL HAROSH SHELI");
            return null;
        }
    }
}
