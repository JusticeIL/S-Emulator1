package program.function;

import XMLandJaxB.SFunction;

import java.io.FileNotFoundException;
import java.util.*;

public class FunctionsContainer {
    private final Map<String, Function> functions = new HashMap<>();
    private final Set<String> functionNames = new HashSet<>();
    private final Map<String, SFunction> sFunctions = new HashMap<>();


    public void setup(Collection<SFunction> sFunctions){
        sFunctions.forEach(sFunction -> {
            this.functionNames.add(sFunction.getName());
            this.sFunctions.put(sFunction.getName(), sFunction);
        });
    }

    public Function tryGetFunction(String name) throws FileNotFoundException {
        if(!functionNames.contains(name)) {
            throw new IllegalArgumentException("Function " + name + " not found");//TODO: CHANGE EXEPTION TYPE LATER
        }

        Function function;

        if(functions.containsKey(name)) {
            function = functions.get(name);
        }
        else{
            function = new Function(sFunctions.get(name),this);
            functions.put(name, function);
        }

        return function;
    }

    public Map<String, Function> getFunctions() {
        return functions;
    }

    public Set<String> getFunctionNames() {
        return functionNames;
    }


}
