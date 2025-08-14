import Engine.Instruction;
import Engine.Label;
import Engine.Program;
import Engine.Variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

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
