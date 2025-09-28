package controller;

import jakarta.xml.bind.JAXBException;
import program.data.ProgramData;
import program.data.VariableDTO;

import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Set;

public interface Model {
    void loadProgram(String path) throws JAXBException, FileNotFoundException;
    boolean isProgramLoaded();
    Optional<ProgramData> getProgramData();
    void Expand(int level) throws IllegalArgumentException;
    void runProgram(Set<VariableDTO> args);
    void startDebug(Set<VariableDTO> args,Set<Integer> breakpoints);
    void stepOver();
    void stopDebug();
    void resumeDebug();
    void switchFunction(String functionName);
}
