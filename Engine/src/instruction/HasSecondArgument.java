package instruction;

import instruction.component.Label;
import instruction.component.Variable;

public interface HasSecondArgument {
    Variable getArgumentVariable();
    int getNumber();
    String getCommand();
    int getCycles();
    Variable getVariable();
    InstructionType getInstructionType();
    Label getLabel();
    Label getDestinationLabel();
}
