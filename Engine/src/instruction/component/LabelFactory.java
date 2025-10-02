package instruction.component;

import java.io.Serializable;

public class LabelFactory implements Serializable {

    private int labelCounter;
    private final String LABEL_PREFIX = "L";

    public LabelFactory() {
        this.labelCounter = 1;
    }

    private Label generateLabel(String labelName) {
        return new Label(labelName);
    }

    public Label createLabel() {
        String labelName = LABEL_PREFIX + labelCounter++;
        return generateLabel(labelName);
    }

    public Label readLabelFromXML(String labelName) {
        if (labelName.startsWith(LABEL_PREFIX)) {
            labelCounter++;
        }
        return generateLabel(labelName);
    }
}