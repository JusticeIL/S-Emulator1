package Engine;

import Engine.XMLandJaxB.SInstruction;
import Engine.XMLandJaxB.SInstructionArgument;
import Engine.XMLandJaxB.SInstructionArguments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InstructionFactory {
    private final Map<String, Variable> variables;
    private final Map<String, Label> labels = new HashMap<>();

    public Instruction GenerateInstruction(SInstruction sInstr, int instructionListLength) {
        Variable variable = GetVariable(sInstr.getSVariable());
        Instruction instruction;
        Label label = getLabelFromSIndtruction(sInstr);
        Label destinationLabel = getDestinationLabelFromSInstruction(sInstr);

        switch(sInstr.getName()){
            case("INCREASE"):
                instruction = new Increase(sInstr,instructionListLength, variable, label, destinationLabel);
                break;
            case("DECREASE"):
                instruction = new Decrease(sInstr,instructionListLength, variable, label, destinationLabel);
                break;
            case("JUMP_NOT_ZERO"):
                instruction = new JumpNotZero(sInstr,instructionListLength, variable, label, destinationLabel);
                break;
            case("NEUTRAL"):
                instruction = new Neutral(sInstr,instructionListLength, variable, label, destinationLabel);
                break;
            default:
                throw new IllegalArgumentException("Invalid Instruction");

        }

        return instruction;
    }

    private Label getLabelFromSIndtruction(SInstruction sInstruction) {
        Label label = Program.EMPTY_LABEL;
        Optional<String> LabelName = Optional.ofNullable(sInstruction.getSLabel());
        return getLabel(label, LabelName);
    }

    private Label getLabel(Label label, Optional<String> labelName) {
        if (labelName.isPresent()) {
            String labelname = labelName.get();
            if (labels.containsKey(labelname)) {
                label = labels.get(labelname);
            } else {
                label = new Label(labelname);
                labels.put(labelname, label);
            }
        }

        return label;
    }

    private Label getDestinationLabelFromSInstruction(SInstruction sInstruction) {
        Label destinationLabel = Program.EMPTY_LABEL;
        SInstructionArguments args = sInstruction.getSInstructionArguments();
        if (args != null) {
            Optional<String> LabelName = Optional.ofNullable(args.getSInstructionArgument().getFirst().getValue());
            destinationLabel = getLabel(destinationLabel, LabelName);
        }
        return destinationLabel;
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
