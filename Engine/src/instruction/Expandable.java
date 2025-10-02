package instruction;

import instruction.component.LabelFactory;
import instruction.component.VariableFactory;

public interface Expandable {

    ExpandedSyntheticInstructionArguments generateExpandedInstructions(LabelFactory labelFactory, VariableFactory variableFactory) throws CloneNotSupportedException;
}