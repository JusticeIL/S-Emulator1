package controller;

import XMLandJaxB.SProgram;
import dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import program.Program;
import program.ProgramExecutioner;
import dto.ProgramData;
import dto.VariableDTO;
import program.function.FunctionsContainer;
import user.User;
import user.UsersManager;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

public class MultiUserController implements MultiUserModel, Serializable {



    private final UsersManager usersManager = new UsersManager();
    private final Map<String, SProgram> loadedPrograms = new HashMap<>();
    private final Map<String, Boolean> isCurrentlyInDebugMode = new HashMap<>();
    private final Map<String, ProgramExecutioner> programExecutionersByUser = new HashMap<>();
    private final FunctionsContainer sharedFunctionsContainer = new FunctionsContainer();

    @Override
    public void loadProgram(String username, InputStream path) throws FileNotFoundException, JAXBException {
        try {

            programExecutionersByUser.putIfAbsent(username, new ProgramExecutioner());
            SProgram sProgram;

            synchronized (loadedPrograms) {

                JAXBContext jaxbContext = JAXBContext.newInstance(SProgram.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                sProgram = (SProgram) jaxbUnmarshaller.unmarshal(path);
                loadedPrograms.putIfAbsent(sProgram.getName(), sProgram);
            }
        } catch (JAXBException e) {
            throw new JAXBException("Error parsing XML file at path: " + path);
        }
    }//TODO: handle loading the same program twice:currently ignores and doesnt raise exception

    @Override
    public boolean isProgramLoaded(String username) {
        return usersManager.getUser(username).getActiveProgram() != null;
    }

    @Override
    public Optional<ProgramData> getProgramData(String username) {
        return Optional.ofNullable(usersManager.getUser(username).getActiveProgram())
                .map(ProgramData::new);
    }

    @Override
    public void Expand(String username, int level) {
        usersManager.getUser(username).ExpandCurrentProgram(level);
    }

    @Override
    public void runProgram(String username, Set<VariableDTO> args) {
        Program activeProgram = usersManager.getUser(username).getActiveProgram();
        ProgramExecutioner programExecutioner = programExecutionersByUser.get(username);
        programExecutioner.setMainExecutioner();
        programExecutioner.setProgram(activeProgram);
        programExecutioner.executeProgram(args);
    }

    @Override
    public void startDebug(String username, Set<VariableDTO> args,Set<Integer> breakpoints) {
        Program activeProgram = usersManager.getUser(username).getActiveProgram();
        ProgramExecutioner programExecutioner = programExecutionersByUser.get(username);
        programExecutioner.setDebugMode(true);
        programExecutioner.setProgram(activeProgram);
        programExecutioner.setUpDebugRun(args, breakpoints);
        isCurrentlyInDebugMode.put(username, true);
    }

    @Override
    public void addBreakpoint(String username, int lineNumber) {
        programExecutionersByUser.get(username).addBreakpoint(lineNumber);
    }

    @Override
    public void removeBreakpoint(String username, int lineNumber) {
        programExecutionersByUser.get(username).removeBreakpoint(lineNumber);
    }

    @Override
    public void stepOver(String username) {
        if(isCurrentlyInDebugMode.get(username)) {
            programExecutionersByUser.get(username).stepOver();
        }
    }



    @Override
    public void stopDebug(String username) {
        if(isCurrentlyInDebugMode.get(username)) {
            programExecutionersByUser.get(username).stopDebug();
            programExecutionersByUser.get(username).setDebugMode(false);
            isCurrentlyInDebugMode.put(username,false);
        }
    }

    @Override
    public void resumeDebug(String username) {
        programExecutionersByUser.get(username).resumeDebug();
    }

    @Override
    public void switchFunction(String username, String functionName) {
        usersManager.getUser(username).switchToFunction(functionName);
    }

    @Override
    public void setActiveProgram(String username, String programName) {
        User user  = usersManager.getUser(username);
        isCurrentlyInDebugMode.putIfAbsent(username, false);
        if(user.hasProgram(programName)){
            user.setActiveProgram(programName);
            return;
        }

        Program newProgramInstance = new Program(loadedPrograms.get(programName), sharedFunctionsContainer);
        usersManager.getUser(username).addProgram(newProgramInstance);
        usersManager.getUser(username).setActiveProgram(programName);
    }

    @Override
    public void addUser(String username) {
        usersManager.addUser(username);
    }

    @Override
    public Set<UserDTO> getAllUsers() {
        return usersManager.getAllUsers();
    }

    @Override
    public void addCredits(String username, int creditsToAdd) {
        usersManager.getUser(username).addCredits(creditsToAdd);
    }

    @Override
    public UserDTO getUserData(String username) {
        return usersManager.getUserData(username);
    }
}