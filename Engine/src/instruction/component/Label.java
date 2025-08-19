package instruction.component;

import java.util.*;

public class Label {
    private static int highestUnusedLabelNumber = 1;
    private final String labelName;

    public Label(String labelName) {
        this.labelName = labelName;
        if (labelName.contains("L")) {
            highestUnusedLabelNumber++;
        }
    }

    public String getLabelName() {
        return labelName;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Label label)) return false;
        return Objects.equals(labelName, label.labelName);
    }

    public Label() {
        this.labelName = "L"+ highestUnusedLabelNumber;
        highestUnusedLabelNumber++;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(labelName);
    }

    @Override
    public String toString() {
        return labelName;
    }
}