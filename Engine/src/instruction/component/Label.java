package instruction.component;

import java.io.Serializable;
import java.util.*;

public class Label implements Serializable {
    private static int highestUnusedLabelNumber = 1;
    private static int previousHighestUnusedLabelNumber = 1;
    private final String labelName;

    public Label(String labelName) {
        this.labelName = labelName;
        if (labelName.contains("L")) {
            highestUnusedLabelNumber++;
        }
    }

    public static void saveHighestUnusedLabelNumber() {
        previousHighestUnusedLabelNumber = highestUnusedLabelNumber;
    }

    public static  void loadHighestUnusedLabelNumber() {
        highestUnusedLabelNumber = previousHighestUnusedLabelNumber;
    }

    public static void resetHighestUnusedLabelNumber() {
        highestUnusedLabelNumber = 1;
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

    public static int getHighestUnusedLabelNumber() {
        return highestUnusedLabelNumber;
    }

    public static void setHighestUnusedLabelNumber(int highestUnusedLabelNumber) {
        Label.highestUnusedLabelNumber = highestUnusedLabelNumber;
    }

    @Override
    public String toString() {
        return labelName;
    }
}