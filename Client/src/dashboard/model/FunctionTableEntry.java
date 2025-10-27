package dashboard.model;

import dto.ProgramData;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FunctionTableEntry {
    private final StringProperty functionName;
    private final StringProperty programOrigin;
    private final StringProperty user;
    private final IntegerProperty instructionsCounter;
    private final IntegerProperty maxProgramLevel;

    public FunctionTableEntry(ProgramData dto) {
        this.functionName = new SimpleStringProperty(dto.getProgramName());
        this.programOrigin = new SimpleStringProperty(dto.getOriginProgram());
        this.user = new SimpleStringProperty(dto.getUploadingUser());
        this.instructionsCounter = new SimpleIntegerProperty(dto.getProgramInstructions().size());
        this.maxProgramLevel = new SimpleIntegerProperty(dto.getMaxExpandLevel());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionTableEntry)) return false;
        FunctionTableEntry other = (FunctionTableEntry) o;
        return this.functionName.get().equals(other.functionName.get()) &&
                this.user.get().equals(other.user.get()) &&
                Integer.valueOf(this.instructionsCounter.get()).equals(other.instructionsCounter.get()) &&
                Integer.valueOf(this.maxProgramLevel.get()).equals(other.maxProgramLevel.get()) &&
                Integer.valueOf(this.instructionsCounter.get()).equals(other.instructionsCounter.get());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(
                functionName.get(),
                user.get(),
                instructionsCounter.get(),
                maxProgramLevel.get(),
                instructionsCounter.get()
        );
    }

    // getters and setters (needed by PropertyValueFactory)
    public String getFunctionName() { return functionName.get(); }
    public void setFunctionName(String name) { functionName.set(name); }
    public StringProperty functionNameProperty() { return functionName; }

    public String getProgramOrigin() { return programOrigin.get(); }
    public void setProgramOrigin(String origin) { programOrigin.set(origin); }
    public StringProperty programOriginProperty() { return programOrigin; }

    public String getUser() { return user.get(); }
    public void setUser(String username) { user.set(username); }
    public StringProperty userProperty() { return user; }

    public int getInstructionsCounter() { return instructionsCounter.get(); }
    public void setInstructionsCounter(int counter) { instructionsCounter.set(counter); }
    public IntegerProperty instructionsCounterProperty() { return instructionsCounter; }

    public int getMaxProgramLevel() { return maxProgramLevel.get(); }
    public void setMaxProgramLevel(int level) { maxProgramLevel.set(level); }
    public IntegerProperty maxProgramLevelProperty() { return maxProgramLevel; }
}
