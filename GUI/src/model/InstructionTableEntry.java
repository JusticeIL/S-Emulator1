package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import program.data.InstructionDTO;

public class InstructionTableEntry {
    private final SimpleStringProperty type;
    private final SimpleStringProperty id;
    private final SimpleStringProperty instruction;
    private final SimpleStringProperty label;
    private final SimpleIntegerProperty cycles;


    public InstructionTableEntry(InstructionDTO instruction) {
        this.type = new SimpleStringProperty(instruction.getType());
        this.id = new SimpleStringProperty(String.valueOf(instruction.getId()));
        this.instruction = new SimpleStringProperty(instruction.getInstruction());
        this.label = new SimpleStringProperty(instruction.getLabel());
        this.cycles = new SimpleIntegerProperty(instruction.getCycles());

    }

    // --- getters for table ---
    public String getType() { return type.get(); }
    public String getId() { return id.get(); }
    public String getInstruction() { return instruction.get(); }
    public String getLabel() { return label.get(); }
    public int getCycles() { return cycles.get(); }

    // --- properties for table ---
    public StringProperty typeProperty() { return type; }
    public StringProperty idProperty() { return id; }
    public StringProperty instructionProperty() { return instruction; }
    public StringProperty labelProperty() { return label; }
    public IntegerProperty cyclesProperty() { return cycles; }


}
