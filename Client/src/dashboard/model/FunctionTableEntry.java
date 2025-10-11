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

    public FunctionTableEntry(ProgramData dto) { //TODO: maybe create function data?
        this.functionName = new SimpleStringProperty(dto.getProgramName());
        this.programOrigin = new SimpleStringProperty("test"); //TODO: dto.getProgramOrigin()
        this.user = new SimpleStringProperty("test"); //TODO: dto.getUser()
        this.instructionsCounter = new SimpleIntegerProperty(0); //TODO: dto.getInstructionsCounter()
        this.maxProgramLevel = new SimpleIntegerProperty(0); //TODO: dto.getMaxProgramLevel()

        /*
        this.programOrigin = new SimpleStringProperty(dto.getProgramOrigin());
        this.user = new SimpleStringProperty(dto.getUser());
        this.instructionsCounter = new SimpleIntegerProperty(dto.getInstructionsCounter());
        this.maxProgramLevel = new SimpleIntegerProperty(dto.getMaxProgramLevel());
        */
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
