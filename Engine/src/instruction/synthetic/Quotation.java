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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quotation extends SyntheticInstruction {

    private Function function;
    List<Variable> arguments;

    public Quotation(int num, Variable variable, int cycles, Label label, Label destinationLabel) {
        super(num, variable, cycles, label, destinationLabel);
    }

    @Override
    protected ExpandedSyntheticInstructionArguments expandSyntheticInstruction(LabelFactory labelFactory, VariableFactory variableFactory) {
        Map<Label,Label> LabelTransitionsOldToNew = new HashMap<>();
        Map<Variable,Variable> VariableTransitionsOldToNew = new HashMap<>();
        List<Instruction> instructions  =new ArrayList<>();
        for (Instruction instruction : function.getInstructionList()) {
            if(!LabelTransitionsOldToNew.containsKey(instruction.getLabel())) {
                LabelTransitionsOldToNew.put(instruction.getLabel(), labelFactory.createLabel());
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
            instructions.add(instruction.duplicate());
//TODO: IMPLEMENT DUPLICATE IN INSTRUCTION
        }
        return null;
    }

    @Override
    protected Label executeUnExpandedInstruction() {
        variable.setValue(function.execute(arguments));
        return Program.EMPTY_LABEL;
    }
}
