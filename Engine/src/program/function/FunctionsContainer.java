package program.function;

import XMLandJaxB.SFunction;
import program.Program;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class FunctionsContainer {

    private final Map<String, Function> functions = new HashMap<>();
    private final Set<String> functionNames = new HashSet<>();
    private final Map<String, SFunction> sFunctions = new HashMap<>();
    private Program originProgram;


    public void setup(Collection<SFunction> sFunctions){
        sFunctions.forEach(sFunction -> {
            this.functionNames.add(sFunction.getName());
            this.sFunctions.putIfAbsent(sFunction.getName(), sFunction);
        });
    }

    public void setup(Collection<SFunction> sFunctions, FunctionsContainer sharedFunctionsContainer,Program originProgram){
        sharedFunctionsContainer.setup(sFunctions);
        sFunctions.forEach(sFunction -> {
            this.functionNames.add(sFunction.getName());
            this.sFunctions.putIfAbsent(sFunction.getName(), sFunction);
        });
        this.originProgram = originProgram;
    }
    public void setup(Collection<SFunction> sFunctions,Program originProgram){
        sFunctions.forEach(sFunction -> {
            this.functionNames.add(sFunction.getName());
            this.sFunctions.putIfAbsent(sFunction.getName(), sFunction);
        });
        this.originProgram = originProgram;
    }

    public Function tryGetFunction(String name) throws FileNotFoundException,IllegalArgumentException {
        if (!functionNames.contains(name)) {
            throw new IllegalArgumentException("Function " + name + " not found");
        }

        Function function;

        if (functions.containsKey(name)) {
            function = functions.get(name);
        }
        else {
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

    public Function tryGetFunction(String name, FunctionsContainer sharedFunctionsContainer) throws FileNotFoundException {



        if (!sharedFunctionsContainer.functionNames.contains(name)) {
            throw new IllegalArgumentException("Function " + name + " not found");
        }

        Function function;

        if (sharedFunctionsContainer.functions.containsKey(name)) {
            function = sharedFunctionsContainer.functions.get(name);
        }
        else {
            setup(sharedFunctionsContainer.sFunctions.values().stream().filter(sFunction -> sFunction.getName().equals(name)).collect(Collectors.toSet()));
            function = new Function(sFunctions.get(name),this,sharedFunctionsContainer,originProgram);
            functions.put(name, function);
            sharedFunctionsContainer.functions.put(name, function);
        }

        return function;
    }

    public void setOriginProgram(Program program) {
        originProgram = program;
    }
}