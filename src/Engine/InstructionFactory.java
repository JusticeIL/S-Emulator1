package Engine;

import Engine.XMLandJaxB.SInstruction;

import java.util.Map;

public class InstructionFactory {
    Map<String, Variable> variableList;
    public Instruction GenerateInstruction(SInstruction sInstr, int instructionListLength) {
        throw new UnsupportedOperationException("Not supported yet.");
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
