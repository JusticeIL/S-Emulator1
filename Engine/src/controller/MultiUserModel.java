package controller;

import dto.UserDTO;
import jakarta.xml.bind.JAXBException;
import dto.ProgramData;
import dto.VariableDTO;
import user.User;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MultiUserModel {
    void loadProgram(String username, InputStream path) throws JAXBException, FileNotFoundException;
    boolean isProgramLoaded(String username);
    Optional<ProgramData> getProgramData(String username);
    void Expand(String username, int level) throws IllegalArgumentException;
    void runProgram(String username, Set<VariableDTO> args, String architectureString);
    void startDebug(String username, Set<VariableDTO> args,Set<Integer> breakpoints, String architectureString);
    void addBreakpoint(String username, int lineNumber);
    void removeBreakpoint(String username, int lineNumber);
    void stepOver(String username);
    void stopDebug(String username);
    void resumeDebug(String username);
    void switchFunction(String username, String functionName);
    void setActiveProgram(String username, String programName);
    void addUser(String username);
    Set<UserDTO> getAllUsers();
    void addCredits(String username, int creditsToAdd);
    UserDTO getUserData(String username);
    List<ProgramData> getAllSharedProgramsData();
    List<ProgramData> getAllSharedFunctionsData();

}