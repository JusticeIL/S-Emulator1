package Engine;

import java.util.*;

public class Label {
    private String labelName;

    public Label(String labelName) {
        this.labelName = labelName;
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