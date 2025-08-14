package Engine;

import java.util.*;

public class Label {
    private Instruction labledInstruction;
    private String labelName;
    public Instruction getLabledInstruction() {
        return labledInstruction;
    }

    public Label(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelName() {
        return labelName;
    }
}