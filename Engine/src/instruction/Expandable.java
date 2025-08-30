package instruction;

public interface Expandable {

    ExpandedSyntheticInstructionArguments generateExpandedInstructions() throws CloneNotSupportedException;
}