package dashboard.model;

import dto.ProgramData;
import javafx.beans.property.*;

public class ProgramTableEntry {
    private final StringProperty programName;
    private final StringProperty user;
    private final IntegerProperty instructionsCounter;
    private final IntegerProperty maxProgramLevel;
    private final IntegerProperty executionsCounter;
    private final DoubleProperty averageExecutionCost;

    public ProgramTableEntry(ProgramData dto) {
        this.programName = new SimpleStringProperty(dto.getProgramName());
        this.user = new SimpleStringProperty(dto.getUploadingUser());
        this.instructionsCounter = new SimpleIntegerProperty(dto.getNumberOfZeroLevelInstructions());
        this.maxProgramLevel = new SimpleIntegerProperty(dto.getMaxExpandLevel());
        this.executionsCounter = new SimpleIntegerProperty(dto.getNumberOfRuns());
        this.averageExecutionCost = new SimpleDoubleProperty(dto.getAvarageCreditsPerRun());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProgramTableEntry)) return false;
        ProgramTableEntry other = (ProgramTableEntry) o;
        return this.programName.get().equals(other.programName.get()) &&
                this.user.get().equals(other.user.get()) &&
                Integer.valueOf(this.instructionsCounter.get()).equals(other.instructionsCounter.get()) &&
                Integer.valueOf(this.maxProgramLevel.get()).equals(other.maxProgramLevel.get()) &&
                Integer.valueOf(this.executionsCounter.get()).equals(other.executionsCounter.get()) &&
                Double.valueOf(this.averageExecutionCost.get()).equals(other.averageExecutionCost.get());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(
                programName.get(),
                user.get(),
                instructionsCounter.get(),
                maxProgramLevel.get(),
                executionsCounter.get(),
                averageExecutionCost.get()
        );
    }

    // getters and setters (needed by PropertyValueFactory)
    public String getProgramName() { return programName.get(); }
    public void setProgramName(String name) { programName.set(name); }
    public StringProperty programNameProperty() { return programName; }

    public String getUser() { return user.get(); }
    public void setUser(String username) { user.set(username); }
    public StringProperty userProperty() { return user; }

    public int getInstructionsCounter() { return instructionsCounter.get(); }
    public void setInstructionsCounter(int counter) { instructionsCounter.set(counter); }
    public IntegerProperty instructionsCounterProperty() { return instructionsCounter; }

    public int getMaxProgramLevel() { return maxProgramLevel.get(); }
    public void setMaxProgramLevel(int level) { maxProgramLevel.set(level); }
    public IntegerProperty maxProgramLevelProperty() { return maxProgramLevel; }

    public int getExecutionsCounter() { return executionsCounter.get(); }
    public void setExecutionsCounter(int counter) { executionsCounter.set(counter); }
    public IntegerProperty executionsCounterProperty() { return executionsCounter; }

    public double getAverageExecutionCost() { return averageExecutionCost.get(); }
    public void setAverageExecutionCost(double cost) { averageExecutionCost.set(cost); }
    public DoubleProperty averageExecutionCostProperty() { return averageExecutionCost; }
}