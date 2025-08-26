package instruction;

import program.Program;

public interface Expandable {

    ExpandedSyntheticInstructionArguments generateExpandedInstructions() throws CloneNotSupportedException;

}