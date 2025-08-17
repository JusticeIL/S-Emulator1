package program;

import instruction.Instruction;

import java.util.List;

public interface Expandable {

    List<Instruction> expand();
}