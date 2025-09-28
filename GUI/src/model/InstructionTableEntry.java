package model;

import javafx.beans.property.*;
import program.data.InstructionDTO;
import program.data.Searchable;

import java.util.HashSet;
import java.util.Set;

public class InstructionTableEntry {
    private final SimpleStringProperty type;
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty instruction;
    private final SimpleStringProperty label;
    private final SimpleStringProperty cycles;
    private final Set<Searchable> searchables;
    private final InstructionDTO instructionDTO;
    private final BooleanProperty breakpoint = new SimpleBooleanProperty(false);

    public InstructionTableEntry(InstructionDTO instruction) {
        searchables = new HashSet<>();
        searchables.add(instruction.getLabel());
        searchables.add(instruction.getVariable());
        searchables.add(instruction.getDestinationLabel());
        searchables.add(instruction.getArgumentVariable());
        searchables.addAll(instruction.getInnerFunctionVariables());

        this.type = new SimpleStringProperty(instruction.getType());
        this.id = new SimpleIntegerProperty(instruction.getId());
        this.instruction = new SimpleStringProperty(instruction.getInstruction());
        this.label = new SimpleStringProperty(instruction.getLabelName());
        this.cycles = new SimpleStringProperty(instruction.getCycles());
        this.instructionDTO = instruction;
    }

    // --- getters for table ---
    public String getType() { return type.get(); }
    public int getId() { return id.get(); }
    public String getInstruction() { return instruction.get(); }
    public String getLabel() { return label.get(); }
    public String getCycles() { return cycles.get(); }
    public boolean isBreakpoint() { return breakpoint.get(); }
    public InstructionDTO getInstructionDTO() { return instructionDTO; }

    // --- properties for table ---
    public StringProperty typeProperty() { return type; }
    public IntegerProperty idProperty() { return id; }
    public StringProperty instructionProperty() { return instruction; }
    public StringProperty labelProperty() { return label; }
    public StringProperty cyclesProperty() { return cycles; }
    public BooleanProperty breakpointProperty() { return breakpoint; }

    public void setBreakpoint(boolean value) { breakpoint.set(value); }

    public Set<Searchable> getSearchables() {
        return searchables;
    }
}
