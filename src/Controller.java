import Engine.Program;
import Engine.Statistics;
import Engine.Variable;

import java.util.List;

public interface Controller {
    Program getProgram();
    boolean loadProgram(String path);
    ProgramData getProgramData(Program program);
    Void Expand(int level);
    List<Variable> RunProgram();
    Statistics getStatistics();

}
