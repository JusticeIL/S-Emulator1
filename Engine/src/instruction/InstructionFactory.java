package instruction;

import XMLandJaxB.SInstructionArgument;
import instruction.basic.Decrease;
import instruction.basic.Increase;
import instruction.basic.JumpNotZero;
import instruction.basic.Neutral;
import instruction.component.LabelFactory;
import instruction.component.VariableFactory;
import instruction.synthetic.*;
import program.Program;
import XMLandJaxB.SInstruction;
import XMLandJaxB.SInstructionArguments;
import instruction.component.Label;
import instruction.component.Variable;
import program.function.Function;

import java.util.*;
import java.util.stream.Collectors;

public class InstructionFactory {

    private final LabelFactory labelFactory;
    private final VariableFactory variableFactory;
    private final Set<Label> destinationLabelSet = new HashSet<>();
    private final Map<String, Label> labels = new HashMap<>();
    private final Set<Label> sourceLabelSet = new HashSet<>();
    private final Map<String, Variable> variables;
    private final Map<String, Function> functions;

    private Variable getVariableFromArguments(SInstructionArguments sInstrArg) {
        if (sInstrArg == null || sInstrArg.getSInstructionArgument() == null) {
            return null;
        }

        return sInstrArg.getSInstructionArgument().stream()
                .filter(arg -> arg != null && arg.getName() != null && arg.getName().toUpperCase().contains("VARIABLE"))
                .filter(arg->!arg.getName().toUpperCase().contains("LABEL"))
                .map(SInstructionArgument::getValue) // may still be null
                .findFirst()
                .map(this::getVariable)
                .orElse(null);
    }

    private int getConstantFromSInstruction(SInstruction sInstr) {
        SInstructionArguments sInstrArg = sInstr.getSInstructionArguments();

        if (sInstrArg == null || sInstrArg.getSInstructionArgument() == null) {
            return 0;
        }
        Optional<String> argumentConstantName = sInstrArg.getSInstructionArgument().stream()
                .filter(arg -> arg != null && arg.getName() != null && arg.getName().toUpperCase().contains("CONSTANTVALUE"))
                .map(SInstructionArgument::getValue) // may still be null
                .findFirst();
        return argumentConstantName.map(Integer::parseInt).orElse(0);
    }

    private Label getLabelFromSInstruction(SInstruction sInstruction) {
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
                label = labelFactory.readLabelFromXML(labelname);
                labels.put(labelname, label);
            }
        }

        return label;
    }

    private Label getDestinationLabelFromSInstruction(SInstruction sInstr) {
        Label destinationLabel = Program.EMPTY_LABEL;
        SInstructionArguments sInstrArg = sInstr.getSInstructionArguments();
        if (sInstrArg == null) {
            return destinationLabel;
        }
        Optional<String> argumentVariableName = sInstrArg.getSInstructionArgument().stream()
                .filter(arg -> arg != null && arg.getName() != null && arg.getName().toUpperCase().contains("LABEL"))
                .map(SInstructionArgument::getValue) // may still be null
                .findFirst();
        return getLabel(destinationLabel, argumentVariableName);
    }

    public Instruction GenerateInstruction(SInstruction sInstr, int instructionCounter) {
        Variable variable = getVariable(sInstr.getSVariable());
        Variable argumentVariable = getVariableFromArguments(sInstr.getSInstructionArguments());
        Instruction instruction;
        Label label = getLabelFromSInstruction(sInstr);
        Label destinationLabel = getDestinationLabelFromSInstruction(sInstr);
        Function function = getFunctionFromSInstruction(sInstr);
        List<Variable> functionArguments = getFunctionArguments(sInstr);

        destinationLabelSet.add(destinationLabel);
        sourceLabelSet.add(label);
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
            case ("GOTO_LABEL") -> new GoToLabel(instructionCounter, variable, label, destinationLabel);
            case("QUOTE") -> new Quotation(instructionCounter,variable,label,function,functionArguments);

            default -> throw new IllegalArgumentException("Invalid Instruction");
        };

        return instruction;
    }

    private List<Variable> getFunctionArguments(SInstruction sInstr) {
        //TODO
        return null;
    }

    private Function getFunctionFromSInstruction(SInstruction sInstr) {
        //TODO
        return null;
    }

    public InstructionFactory(Map<String, Variable> variables, LabelFactory labelFactory, VariableFactory variableFactory, Map<String, Function> functions) {
        this.variables = variables;
        this.labelFactory = labelFactory;
        this.variableFactory = variableFactory;
        this.functions = functions;
    }

    Variable getVariable(String variableName) {
        variableName = variableName.toLowerCase();
        try {
            if (variables.containsKey(variableName)) {
                return variables.get(variableName);
            } else {
                Variable variable = variableFactory.generateVariable(variableName, 0);
                variables.put(variableName, variable);
                return variable;
            }
            } catch (NumberFormatException e) {
            return null;
        }
    }

    public Set<Label> getMissingLabels() {
        return destinationLabelSet.stream().filter(label -> !sourceLabelSet.contains(label))
                .filter(label -> !label.equals(Program.EXIT_LABEL)).collect(Collectors.toSet());
    }

    public Instruction GenerateExitInstruction(int size) {
        return new Neutral(size + 1, variableFactory.generateVariable("Exit", 0), Program.EXIT_LABEL, Program.EXIT_LABEL);
    }
}