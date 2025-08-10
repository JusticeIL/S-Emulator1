package Engine;

import java.util.List;

public class PC {

    private Command currentCommand;
    private List<Command> commandList;
    private String EXIT_LABEL = "EXIT";

    private void update() {

    }

    public void executeCurrentCommand() {
        String nextLabel = currentCommand.execute();
        if (nextLabel == "") {
            // Case: no label
        }
        else if (nextLabel == EXIT_LABEL) {
            //Case: exit command
        }
        else {
            //The rest
        }
    }

}
