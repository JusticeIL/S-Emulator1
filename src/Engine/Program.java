package Engine;

import java.util.List;

public class Program {

    private Instruction currentInstruction;
    private List<Instruction> instructionList;
    private String EXIT_LABEL = "EXIT";
    int PC; // Program Counter
    int cycleCounter;

    private void update() {

    }

    public void executeCurrentCommand() {
        String nextLabel = currentInstruction.execute();
        if (nextLabel.equals("")) {
            // Case: no label
        }
        else if (nextLabel.equals(EXIT_LABEL)) {
            // Case: exit command
        }
        else {
            // The rest
        }
    }

}
