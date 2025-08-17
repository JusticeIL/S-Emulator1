package program;

import instruction.Instruction;
import instruction.component.Label;
import instruction.component.Variable;

import java.util.ArrayList;
import java.util.List;

public final class ProgramData {
    private final String programName;
    private final List<String> programArguments = new ArrayList<>();
    private final List<String> programLabels = new ArrayList<>();
    private final List<String> programInstructions = new ArrayList<>();

    public ProgramData(Program program) {
        this.programName = program.getProgramName();
        for (Instruction instruction : program.getInstructionList()) {
            programInstructions.add(instruction.toString());
        }
        for (Variable variable : program.getVariables()) {
            if(variable.getName().contains("x")){
                programArguments.add(variable.getName());
            }
        }
        for (Label label : program.getLabels()) {
            programLabels.add(label.toString());
        }
    }

    public String getProgramName() {
        return programName;
    }

    public List<String> getProgramArguments() {
        return programArguments;
    }

    public List<String> getProgramLabels() {
        return programLabels;
    }

    public List<String> getProgramInstructions() {
        return programInstructions;
    }
}
