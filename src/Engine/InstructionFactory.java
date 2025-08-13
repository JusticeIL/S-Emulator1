package Engine;

import Engine.XMLandJaxB.SInstruction;

import java.util.Map;

public class InstructionFactory {
    Map<String, Variable> variableList;
    public Instruction GenerateInstruction(SInstruction sInstr, int instructionListLength) {
        Variable variable = GetVariable(sInstr.getSVariable());
        Instruction instruction;
        switch(sInstr.getName()){
            case("Increase"):
                instruction = new Increase(sInstr,instructionListLength, variable);
            case("Decrease"):
                instruction = new Decrease(sInstr,instructionListLength, variable);
            case("Jump Not Zero"):
                instruction = new JumpNotZero(sInstr,instructionListLength, variable);
            case("Neutral"):
                instruction = new Neutral(sInstr,instructionListLength, variable);
            default:
                throw  new IllegalArgumentException("Invalid Instruction");
        }


    }

    InstructionFactory(Map<String, Variable> variableList) {
        this.variableList = variableList;
    }

    Variable GetVariable(String variableName) {
        if (variableList.containsKey(variableName)) {
            return variableList.get(variableName);
        }else{
            Variable variable = new Variable();
            variableList.put(variableName, variable);
            return variable;
        }
    }

}
