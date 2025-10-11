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
        this.user = new SimpleStringProperty("test"); //TODO: dto.getUser()
        this.instructionsCounter = new SimpleIntegerProperty(0); //TODO: dto.getInstructionsCounter()
        this.maxProgramLevel = new SimpleIntegerProperty(0); //TODO: dto.getMaxProgramLevel()
        this.executionsCounter = new SimpleIntegerProperty(0); //TODO: dto.getExecutionsCounter()
        this.averageExecutionCost = new SimpleDoubleProperty(0.0); //TODO: dto.getAverageExecutionCost()

        /*
        this.user = new SimpleStringProperty(dto.getUser());
        this.instructionsCounter = new SimpleIntegerProperty(dto.getInstructionsCounter());
        this.maxProgramLevel = new SimpleIntegerProperty(dto.getMaxProgramLevel());
        this.executionsCounter = new SimpleIntegerProperty(dto.getExecutionsCounter());
        this.averageExecutionCost = new SimpleDoubleProperty(dto.getAverageExecutionCost());
        */
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