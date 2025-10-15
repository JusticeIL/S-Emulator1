package controller;

import jakarta.xml.bind.JAXBException;
import dto.ProgramData;
import dto.VariableDTO;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

public interface MultiUserModel {
    void loadProgram(String username, InputStream path) throws JAXBException, FileNotFoundException;
    boolean isProgramLoaded(String username);
    Optional<ProgramData> getProgramData(String username);
    void Expand(String username, int level) throws IllegalArgumentException;
    void runProgram(String username, Set<VariableDTO> args);
    void startDebug(String username, Set<VariableDTO> args,Set<Integer> breakpoints);
    void addBreakpoint(String username, int lineNumber);
    void removeBreakpoint(String username, int lineNumber);
    void stepOver(String username);
    void stopDebug(String username);
    void resumeDebug(String username);
    void switchFunction(String username, String functionName);
}