package execution.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import dto.VariableDTO;

public class ArgumentTableEntry {
    private final StringProperty name;
    private final IntegerProperty value;

    public ArgumentTableEntry(String name) {
        this.name = new SimpleStringProperty(name);
        this.value = new SimpleIntegerProperty(0);
    }

    public ArgumentTableEntry(VariableDTO variable) {
        this.name = new SimpleStringProperty(variable.getName());
        this.value = new SimpleIntegerProperty(variable.getValue());
    }

    // getters and setters (needed by PropertyValueFactory)
    public String getName() { return name.get(); }
    public void setName(String n) { name.set(n); }
    public StringProperty nameProperty() { return name; }

    public int getValue() { return value.get(); }
    public void setValue(int v) { value.set(v); }
    public IntegerProperty valueProperty() { return value; }
}
