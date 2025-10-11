package execution.model;

import javafx.beans.property.*;
import dto.InstructionDTO;
import dto.Searchable;

import java.util.HashSet;
import java.util.Set;

public class InstructionTableEntry {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty type;
    private final SimpleStringProperty label;
    private final SimpleStringProperty instruction;
    private final SimpleStringProperty cycles;
    private final SimpleStringProperty architecture;
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

        this.id = new SimpleIntegerProperty(instruction.getId());
        this.type = new SimpleStringProperty(instruction.getType());
        this.label = new SimpleStringProperty(instruction.getLabelName());
        this.instruction = new SimpleStringProperty(instruction.getInstruction());
        this.cycles = new SimpleStringProperty(instruction.getCycles());
        this.architecture = new SimpleStringProperty("test"); //TODO: implement instruction.getArchitecture()
        this.instructionDTO = instruction;
    }

    // --- getters for table ---
    public int getId() { return id.get(); }
    public String getType() { return type.get(); }
    public String getLabel() { return label.get(); }
    public String getInstruction() { return instruction.get(); }
    public String getCycles() { return cycles.get(); }
    public String getArchitecture() { return architecture.get(); }
    public boolean isBreakpoint() { return breakpoint.get(); }
    public InstructionDTO getInstructionDTO() { return instructionDTO; }

    // --- properties for table ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty typeProperty() { return type; }
    public StringProperty labelProperty() { return label; }
    public StringProperty instructionProperty() { return instruction; }
    public StringProperty cyclesProperty() { return cycles; }
    public StringProperty architectureProperty() { return architecture; }
    public BooleanProperty breakpointProperty() { return breakpoint; }

    public void setBreakpoint(boolean value) { breakpoint.set(value); }

    public Set<Searchable> getSearchables() {
        return searchables;
    }
}