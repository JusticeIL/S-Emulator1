package instruction.synthetic;

import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.SyntheticInstruction;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;
import program.Program;
import program.function.Function;

import java.util.*;
import java.util.stream.Collectors;

public class Quotation extends SyntheticInstruction {

    private final Function function;
    private final List<Variable> arguments = new ArrayList<>();

    public Quotation(int num, Variable variable, Label label,Function function,List<Variable> arguments) {
        super(num, variable, function.getProgramCycles(), label, Program.EMPTY_LABEL);
        this.function = function;
        this.arguments.addAll(arguments);
    }

    @Override
    protected ExpandedSyntheticInstructionArguments expandSyntheticInstruction(LabelFactory labelFactory, VariableFactory variableFactory) {
        Map<Label,Label> LabelTransitionsOldToNew = new HashMap<>();
        Map<Variable,Variable> VariableTransitionsOldToNew = new HashMap<>();
        List<Instruction> instructions  =new ArrayList<>();

        Map<Label,Instruction> labelMap = new  HashMap<>();
        for (Instruction instruction : function.getInstructionList()) {
            if(!LabelTransitionsOldToNew.containsKey(instruction.getLabel())) {
                LabelTransitionsOldToNew.put(instruction.getLabel(), labelFactory.createLabel());
                labelMap.put(LabelTransitionsOldToNew.get(instruction.getLabel()), instruction);
            }
            if(!LabelTransitionsOldToNew.containsKey(instruction.getDestinationLabel())) {
                LabelTransitionsOldToNew.put(instruction.getDestinationLabel(), labelFactory.createLabel());
            }
            if(!VariableTransitionsOldToNew.containsKey(instruction.getVariable())) {
                VariableTransitionsOldToNew.put(instruction.getVariable(),variableFactory.createZVariable());
            }
            if(!VariableTransitionsOldToNew.containsKey(instruction.getArgumentVariable())) {
                VariableTransitionsOldToNew.put(instruction.getArgumentVariable(),variableFactory.createZVariable());
            }
            Variable newVariable = VariableTransitionsOldToNew.get(instruction.getVariable());
            Variable newArgumentVariable = VariableTransitionsOldToNew.get(instruction.getArgumentVariable());
            Label newLabel = LabelTransitionsOldToNew.get(instruction.getLabel());
            Label newDestinationLabel = LabelTransitionsOldToNew.get(instruction.getDestinationLabel());
            instructions.add(instruction.duplicate(newVariable,newArgumentVariable,newLabel,newDestinationLabel));
        }
        Set<Variable> newVariables = new HashSet<>(VariableTransitionsOldToNew.values());

        return new ExpandedSyntheticInstructionArguments(newVariables,labelMap,instructions);
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        variable.setValue(function.execute(arguments));
        return Program.EMPTY_LABEL;
    }

    @Override
    public Instruction duplicate(Variable newVariable, Variable newArgumentVariable, Label newLabel, Label newDestinationLabel) {
        return  new Quotation(number,newVariable,newLabel,function ,arguments);
    }
}
