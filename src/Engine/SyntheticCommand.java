package Engine;

import java.util.List;

abstract public class SyntheticCommand extends Command {

    List<Variable> variables;

    abstract public List<Command> extend(); //Implement
}