package dto;

import user.User;

public class UserDTO {
    private final String username;
    private final int programsLoaded;
    private final int functionsLoaded;
    private final int credits;
    private final int creditsUsed;
    private final int programExecutionsCounter;
    private final Statistics history;

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.programsLoaded = user.getProgramsLoaded();
        this.functionsLoaded = user.getFunctionsLoaded();
        this.credits = user.getCredits();
        this.creditsUsed = user.getCreditsUsed();
        this.programExecutionsCounter = user.getProgramExecutionsCounter();
        this.history = user.getHistory();
    }

    public String getUsername() {
        return username;
    }
}
