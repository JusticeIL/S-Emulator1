package program.function;

import XMLandJaxB.SFunction;
import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;
import program.Program;
import program.ProgramExecutioner;
import program.data.VariableDTO;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Function extends Program {

    private final String userString;

    public Function(SFunction sFunction,FunctionsContainer functionsContainer) throws FileNotFoundException {
        super(sFunction.getSInstructions(), sFunction.getName(),functionsContainer);
        this.userString = sFunction.getUserString();
    }

    public String getUserString() {
        return userString;
    }

    public int execute(List<FunctionArgument> arguments) {
        ProgramExecutioner programExecutioner = new ProgramExecutioner();
        programExecutioner.setProgram(this);
        List<VariableDTO> verifiedArgumentList = new ArrayList<>();
        for (FunctionArgument argument : arguments) {
            int argumentCounter = 1;
            VariableDTO functionArgument = new VariableDTO(argument);
            if(functionArgument.getName().contains("(")){
                functionArgument.setName("x"+argumentCounter);
            }
            verifiedArgumentList.add(functionArgument);
            argumentCounter++;
        }

        programExecutioner.executeProgram(new HashSet<>(verifiedArgumentList));
        return getVariables().stream().filter(var->var.getName().equals("y")).toList().getFirst().getValue();
    }

    public ExpandedSyntheticInstructionArguments open(LabelFactory labelFactory, VariableFactory variableFactory, Map<Label,Label> LabelTransitionsOldToNew , Map<String, Variable> VariableTransitionsOldToNew){
        List<Instruction> instructions = new ArrayList<>();
        Map<Label,Instruction> labelMap = new  HashMap<>();
        LabelTransitionsOldToNew.put(Program.EMPTY_LABEL,Program.EMPTY_LABEL);

        for (Instruction instruction : getInstructionList()) {
            if(!LabelTransitionsOldToNew.containsKey(instruction.getLabel())) {
                LabelTransitionsOldToNew.put(instruction.getLabel(), labelFactory.createLabel());
                labelMap.put(LabelTransitionsOldToNew.get(instruction.getLabel()), instruction);
            }
            if(!LabelTransitionsOldToNew.containsKey(instruction.getDestinationLabel())) {
                LabelTransitionsOldToNew.put(instruction.getDestinationLabel(), labelFactory.createLabel());
            }
            if(!VariableTransitionsOldToNew.containsKey(instruction.getVariable().getName())) {
                VariableTransitionsOldToNew.put(instruction.getVariable().getName(),variableFactory.createZVariable());
            }
            if(!VariableTransitionsOldToNew.containsKey(instruction.getArgumentVariable().getName())) {
                VariableTransitionsOldToNew.put(instruction.getArgumentVariable().getName(),variableFactory.createZVariable());
            }
            Variable newVariable = VariableTransitionsOldToNew.get(instruction.getVariable().getName());
            Variable newArgumentVariable = VariableTransitionsOldToNew.get(instruction.getArgumentVariable().getName());
            Label newLabel = LabelTransitionsOldToNew.get(instruction.getLabel());
            Label newDestinationLabel = LabelTransitionsOldToNew.get(instruction.getDestinationLabel());
            instructions.add(instruction.duplicate(newVariable,newArgumentVariable,newLabel,newDestinationLabel));
        }
        Set<Variable> newVariables = new HashSet<>(VariableTransitionsOldToNew.values());

        return new ExpandedSyntheticInstructionArguments(newVariables,labelMap,instructions);
    }

    public String getName() {
        return getUserString();
    }
}
