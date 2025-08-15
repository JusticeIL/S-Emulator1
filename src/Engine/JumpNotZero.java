package Engine;

import Engine.XMLandJaxB.SInstruction;

public class JumpNotZero extends BasicInstruction {

    static private final int CYCLES = 2;

    public JumpNotZero(SInstruction sInstruction, int num, Variable variable, Label label, Label destinationLabel) {
        super(sInstruction, num, variable, CYCLES, label, destinationLabel);
        String newDestLabelName = sInstruction.getSInstructionArguments().getSInstructionArgument().getFirst().getValue();
        parseDestinationLabel(sInstruction);
        command = "IF " + variable.getName() + "!=0" + " GOTO " + destinationLabel.getLabelName();
    }

    @Override
    public Label execute() {
        if (variable.getValue() != 0) {
            return destinationLabel;
        }
        else
            return null; // No jump, handle later
    }
}