package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VariableEntry {
    private final StringProperty name;
    private final IntegerProperty value;

    public VariableEntry(String name, Integer value) {
        this.name = new SimpleStringProperty(name);
        this.value = new SimpleIntegerProperty(value);
    }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public int getValue() { return value.get(); }
    public IntegerProperty valueProperty() { return value; }
}
