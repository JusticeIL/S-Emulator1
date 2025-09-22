package program.function;

import java.util.List;

public interface FunctionArgument {

    int getValue();

    String getName();

    Function tryGetFunction();

    List<FunctionArgument> tryGetFunctionArguments();
}
