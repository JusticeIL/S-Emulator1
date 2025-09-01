package instruction.component;

import java.io.Serializable;
import java.util.*;

public class Label implements Serializable {

    private final String labelName;

    public Label(int highestUnusedLabelNumber) {
        this.labelName = "L"+ highestUnusedLabelNumber;
    }

    public Label(String labelName) {
        this.labelName = labelName;
    }

    @Override
    public String toString() {
        return labelName;
    }

    public String getLabelName() {
        return labelName;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Label label)) return false;
        return Objects.equals(labelName, label.labelName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(labelName);
    }
}