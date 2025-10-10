
public class User {
    private int programsLoaded;
    private int functionsLoaded;
    private int credits;
    private int creditsUsed;
    private int programExecutionsCounter;
    private final String username;
    private final Statistics history;

    public User(String username) {
        this.username = username;
        this.programsLoaded = 0;
        this.functionsLoaded = 0;
        this.credits = 0;
        this.creditsUsed = 0;
        this.programExecutionsCounter = 0;
        this.history = new Statistics();
    }

    public String getUsername() {
        return username;
    }

    public int getProgramsLoaded() {
        return programsLoaded;
    }

    public int getFunctionsLoaded() {
        return functionsLoaded;
    }

    public int getCredits() {
        return credits;
    }

    public int getCreditsUsed() {
        return creditsUsed;
    }

    public int getProgramExecutionsCounter() {
        return programExecutionsCounter;
    }

    public Statistics getHistory() {
        return history;
    }
}
