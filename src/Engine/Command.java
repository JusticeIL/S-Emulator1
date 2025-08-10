package Engine;

abstract public class Command {

    private int number;
    private String label;
    private String command;
    private int cycles;
    private String destinationLabel;
    private int level;

    public abstract String execute();
        // Implementation of command execution logic

}
