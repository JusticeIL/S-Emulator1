package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import program.data.InstructionDTO;
import program.data.Searchable;

import java.util.HashSet;
import java.util.Set;

public class InstructionTableEntry {
    private final SimpleStringProperty type;
    private final SimpleStringProperty id;
    private final SimpleStringProperty instruction;
    private final SimpleStringProperty label;
    private final SimpleIntegerProperty cycles;
    private final Set<Searchable> searchables;
    private final InstructionDTO instructionDTO;

    public InstructionTableEntry(InstructionDTO instruction) {
        searchables = new HashSet<>();
        searchables.add(instruction.getLabel());
        searchables.add(instruction.getVariable());
        searchables.add(instruction.getDestinationLabel());
        searchables.add(instruction.getArgumentVariable());
        searchables.addAll(instruction.getInnerFunctionVariables());

        this.type = new SimpleStringProperty(instruction.getType());
        this.id = new SimpleStringProperty(String.valueOf(instruction.getId()));
        this.instruction = new SimpleStringProperty(instruction.getInstruction());
        this.label = new SimpleStringProperty(instruction.getLabelName());
        this.cycles = new SimpleIntegerProperty(instruction.getCycles());

        this.instructionDTO = instruction;
    }

    // --- getters for table ---
    public String getType() { return type.get(); }
    public String getId() { return id.get(); }
    public String getInstruction() { return instruction.get(); }
    public String getLabel() { return label.get(); }
    public int getCycles() { return cycles.get(); }
    public InstructionDTO getInstructionDTO() { return instructionDTO; }

    // --- properties for table ---
    public StringProperty typeProperty() { return type; }
    public StringProperty idProperty() { return id; }
    public StringProperty instructionProperty() { return instruction; }
    public StringProperty labelProperty() { return label; }
    public IntegerProperty cyclesProperty() { return cycles; }

    public Set<Searchable> getSearchables() {
        return searchables;
    }
}
