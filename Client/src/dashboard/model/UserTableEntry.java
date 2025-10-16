package dashboard.model;

import dto.UserDTO;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserTableEntry implements Comparable<UserTableEntry> {
    private final StringProperty username;
    private final IntegerProperty programsLoaded;
    private final IntegerProperty functionsLoaded;
    private final IntegerProperty credits;
    private final IntegerProperty creditsUsed;
    private final IntegerProperty programExecutionsCounter;

    public UserTableEntry(UserDTO user) {
        this.username = new SimpleStringProperty(user.getUsername());
        this.programsLoaded = new SimpleIntegerProperty(user.getProgramsLoaded());
        this.functionsLoaded = new SimpleIntegerProperty(user.getFunctionsLoaded());
        this.credits = new SimpleIntegerProperty(user.getCredits());
        this.creditsUsed = new SimpleIntegerProperty(user.getCreditsUsed());
        this.programExecutionsCounter = new SimpleIntegerProperty(user.getProgramExecutionsCounter());
    }

    @Override
    public int compareTo(UserTableEntry other) {
        return this.username.get().compareToIgnoreCase(other.getUsername());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTableEntry)) return false;
        UserTableEntry other = (UserTableEntry) o;
        return this.username.get().equals(other.username.get()) &&
                Integer.valueOf(this.programsLoaded.get()).equals(other.programsLoaded.get()) &&
                Integer.valueOf(this.functionsLoaded.get()).equals(other.functionsLoaded.get()) &&
                Integer.valueOf(this.credits.get()).equals(other.credits.get()) &&
                Integer.valueOf(this.creditsUsed.get()).equals(other.creditsUsed.get()) &&
                Integer.valueOf(this.programExecutionsCounter.get()).equals(other.programExecutionsCounter.get());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(
                username.get(),
                programsLoaded.get(),
                functionsLoaded.get(),
                credits.get(),
                creditsUsed.get(),
                programExecutionsCounter.get()
        );
    }

    // getters and setters (needed by PropertyValueFactory)
    public String getUsername() { return username.get(); }
    public void setUsername(String name) { username.set(name); }
    public StringProperty usernameProperty() { return username; }

    public int getProgramsLoaded() { return programsLoaded.get(); }
    public void setProgramsLoaded(int programsLoadedAmount) { programsLoaded.set(programsLoadedAmount); }
    public IntegerProperty programsLoadedProperty() { return programsLoaded; }

    public int getFunctionsLoaded() { return functionsLoaded.get(); }
    public void setFunctionsLoaded(int functionsLoadedAmount) { functionsLoaded.set(functionsLoadedAmount); }
    public IntegerProperty functionsLoadedProperty() { return functionsLoaded; }

    public int getCredits() { return credits.get(); }
    public void setCredits(int creditsAmount) { credits.set(creditsAmount); }
    public IntegerProperty creditsProperty() { return credits; }

    public int getCreditsUsed() { return creditsUsed.get(); }
    public void setCreditsUsed(int creditsUsedAmount) { creditsUsed.set(creditsUsedAmount); }
    public IntegerProperty creditsUsedProperty() { return creditsUsed; }

    public int getProgramExecutionsCounter() { return programExecutionsCounter.get(); }
    public void setProgramExecutionsCounter(int executions) { programExecutionsCounter.set(executions); }
    public IntegerProperty programExecutionsCounterProperty() { return programExecutionsCounter; }
}