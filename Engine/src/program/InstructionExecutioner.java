package program;

import instruction.Instruction;
import instruction.component.Label;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructionExecutioner {

    public static void executeInstructions(List<Instruction> instructions,Map<Label, Instruction> labels) {
        if (instructions == null || instructions.isEmpty()) return;

        // Build label-to-instruction map
        int currentIndex = 0;
        Instruction currentInstruction = instructions.get(currentIndex);

        while (currentIndex < instructions.size()) {
            Label nextLabel = currentInstruction.execute();

            if (nextLabel.equals(Program.EMPTY_LABEL)) {
                currentIndex++;
            } else if (nextLabel.equals(Program.EXIT_LABEL)) {
                currentIndex = instructions.size(); // Exit the loop
            } else {
                currentInstruction = labels.get(nextLabel);
                currentIndex = currentInstruction.getNumber() - 1;
            }

            if (currentIndex < instructions.size()) {
                currentInstruction = instructions.get(currentIndex);
            }
        }
    }
}
