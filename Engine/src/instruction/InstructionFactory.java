package instruction;

import XMLandJaxB.SInstructionArgument;
import instruction.basic.Decrease;
import instruction.basic.Increase;
import instruction.basic.JumpNotZero;
import instruction.basic.Neutral;
import instruction.synthetic.*;
import program.Program;
import XMLandJaxB.SInstruction;
import XMLandJaxB.SInstructionArguments;
import instruction.component.Label;
import instruction.component.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InstructionFactory {
    private final Map<String, Variable> variables;
    private final Map<String, Label> labels = new HashMap<>();

    public Instruction GenerateInstruction(SInstruction sInstr, int instructionCounter) {
        Variable variable = getVariable(sInstr.getSVariable());
        Variable argumentVariable = getVariableFromArguments(sInstr.getSInstructionArguments());
        Instruction instruction;
        Label label = getLabelFromSIndtruction(sInstr);
        Label destinationLabel = getDestinationLabelFromSInstruction(sInstr);

        int constant = getConstantFromSInstruction(sInstr);

        instruction = switch (sInstr.getName().toUpperCase()) {
            case ("INCREASE") -> new Increase(instructionCounter, variable, label, destinationLabel);
            case ("DECREASE") -> new Decrease(instructionCounter, variable, label, destinationLabel);
            case ("JUMP_NOT_ZERO") -> new JumpNotZero(instructionCounter, variable, label, destinationLabel);
            case ("NEUTRAL") -> new Neutral(instructionCounter, variable, label, destinationLabel);
            case("JUMP_ZERO") -> new JumpZero(instructionCounter, variable, label, destinationLabel);
            case("ZERO_VARIABLE") -> new ZeroVariable(instructionCounter, variable, label, destinationLabel);
            case ("JUMP_EQUAL_CONSTANT") -> new JumpEqualConstant(instructionCounter, variable, label, destinationLabel, constant);
            case ("CONSTANT_ASSIGNMENT") -> new ConstantAssignment(instructionCounter, variable, label, destinationLabel, constant);
            case ("JUMP_EQUAL_VARIABLE") -> new JumpEqualVariable(instructionCounter, variable, label, destinationLabel, argumentVariable);
            case ("ASSIGNMENT") -> new Assignment(instructionCounter, variable, label, destinationLabel, argumentVariable);
            case ("GO_TO_LABEL") -> new GoToLabel(instructionCounter, variable, label, destinationLabel);

            default -> throw new IllegalArgumentException("Invalid Instruction");
        };

        return instruction;
    }

    private Variable getVariableFromArguments(SInstructionArguments sInstrArg) {
        //returns the second argument of the instruction, if exists
        if (sInstrArg == null || sInstrArg.getSInstructionArgument().size() < 2) {
            return null; // No second argument, return null
        }
        Optional<String> argumentVariableName = Optional.ofNullable(sInstrArg.getSInstructionArgument().get(1).getValue());
        if (argumentVariableName.isPresent()) {
            String argumentVariable = argumentVariableName.get();
            return getVariable(argumentVariable);
        } else {
            return null; // No second argument, return null
        }

    }

    private int getConstantFromSInstruction(SInstruction sInstr) {
        int constant = 0;
        SInstructionArguments args = sInstr.getSInstructionArguments();
        if (args != null && !args.getSInstructionArgument().isEmpty()) {
            String value = args.getSInstructionArgument().getFirst().getValue();
            try {
                constant = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Invalid constant value: " + value);
            }
        }
        return constant;
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

    public InstructionFactory(Map<String, Variable> variables) {
        this.variables = variables;
    }

    Variable getVariable(String variableName) {
        try {
            if (variables.containsKey(variableName)) {
                return variables.get(variableName);
            }else{
                Variable variable = new Variable(variableName, 0);
                variables.put(variableName, variable);
                return variable;
            }
            } catch (NumberFormatException e) {
            System.out.println("HARA AL HAROSH SHELI");
            return null;
        }
    }
}
