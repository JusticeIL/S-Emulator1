package controller;

import XMLandJaxB.SFunctions;
import XMLandJaxB.SProgram;
import dto.UserDTO;
import dto.ArchitectureGeneration;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import dto.ProgramData;
import dto.VariableDTO;
import user.User;
import user.UsersManager;

import javax.naming.InsufficientResourcesException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

public class MultiUserController implements MultiUserModel, Serializable {


    private final SharedProgramsContainer sharedProgramsContainer = new SharedProgramsContainer();
    private final UsersManager usersManager = new UsersManager();
    private final ExecutionManager executionManager = new ExecutionManager();

    @Override
    public void loadProgram(String username, InputStream path) throws FileNotFoundException, JAXBException {

        try {
            synchronized (sharedProgramsContainer) {
                JAXBContext jaxbContext = JAXBContext.newInstance(SProgram.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                SProgram sProgram = (SProgram) jaxbUnmarshaller.unmarshal(path);
                if (sharedProgramsContainer.getAllProgramNames().contains(sProgram.getName())) {
                    throw new IllegalArgumentException("Program already loaded");
                }

                sharedProgramsContainer.addSProgram(sProgram, username);
            }
            usersManager.getUser(username).updateProgramsLoaded();
            usersManager.getUser(username).setFunctionsLoaded(sharedProgramsContainer.getNumberOfFunctions(username));
        } catch (JAXBException e) {
            throw new JAXBException("Error parsing XML file at path: " + path);
        }
    }

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
    public void runProgram(String username, Set<VariableDTO> args, String architectureString) throws InsufficientResourcesException {
        User user = usersManager.getUser(username);
        ArchitectureGeneration architectureGeneration = ArchitectureGeneration.valueOf(architectureString);
        executionManager.CheckForCreditsAboveProgramAverage(user, architectureGeneration, sharedProgramsContainer);
        executionManager.runProgram(user, args, architectureGeneration);
        String programName = user.getActiveProgram().getProgramName();
        int CostForLastExecution = executionManager.getCostForLastExecution(user);
        sharedProgramsContainer.addRunForProgram(programName, CostForLastExecution);
        if (executionManager.checkInsufficientCredits(user)) {
            throw new InputMismatchException();
        }
    }

    @Override
    public void startDebug(String username, Set<VariableDTO> args, Set<Integer> breakpoints, String architectureString) throws InsufficientResourcesException {
        User user = usersManager.getUser(username);
        ArchitectureGeneration architectureGeneration = ArchitectureGeneration.valueOf(architectureString);
        executionManager.startDebug(user, args, breakpoints, architectureGeneration);
        if (executionManager.checkInsufficientCredits(user)) {
            int CostForLastExecution = executionManager.getCostForLastExecution(user);
            String programName = user.getActiveProgram().getProgramName();
            sharedProgramsContainer.addRunForProgram(programName, CostForLastExecution);
            throw new InputMismatchException();
        }
        if (!executionManager.isInDebugMode(user)) {
            int CostForLastExecution = executionManager.getCostForLastExecution(user);
            String programName = user.getActiveProgram().getProgramName();
            sharedProgramsContainer.addRunForProgram(programName, CostForLastExecution);
        }
    }

    @Override
    public void addBreakpoint(String username, int lineNumber) {
        User user = usersManager.getUser(username);
        executionManager.addBreakpoint(user, lineNumber);
    }

    @Override
    public void removeBreakpoint(String username, int lineNumber) {
        User user = usersManager.getUser(username);
        executionManager.removeBreakpoint(user, lineNumber);
    }

    @Override
    public void stepOver(String username) throws InsufficientResourcesException {
        User user = usersManager.getUser(username);
        executionManager.stepOver(user);
        if (executionManager.checkInsufficientCredits(user)) {
            int CostForLastExecution = executionManager.getCostForLastExecution(user);
            String programName = user.getActiveProgram().getProgramName();
            sharedProgramsContainer.addRunForProgram(programName, CostForLastExecution);
            throw new InputMismatchException();
        }
        if (!executionManager.isInDebugMode(user)) {
            int CostForLastExecution = executionManager.getCostForLastExecution(user);
            String programName = user.getActiveProgram().getProgramName();
            sharedProgramsContainer.addRunForProgram(programName, CostForLastExecution);
        }
    }

    @Override
    public void stopDebug(String username) {
        User user = usersManager.getUser(username);
        executionManager.stopDebug(user);
        int CostForLastExecution = executionManager.getCostForLastExecution(user);
        String programName = user.getActiveProgram().getProgramName();
        sharedProgramsContainer.addRunForProgram(programName, CostForLastExecution);
    }

    @Override
    public void resumeDebug(String username) throws InsufficientResourcesException {
        User user = usersManager.getUser(username);
        executionManager.resumeDebug(user);

        if (executionManager.checkInsufficientCredits(user)) {
            int CostForLastExecution = executionManager.getCostForLastExecution(user);
            String programName = user.getActiveProgram().getProgramName();
            sharedProgramsContainer.addRunForProgram(programName, CostForLastExecution);
            throw new InputMismatchException();
        }
        if (!executionManager.isInDebugMode(user)) {
            int CostForLastExecution = executionManager.getCostForLastExecution(user);
            String programName = user.getActiveProgram().getProgramName();
            sharedProgramsContainer.addRunForProgram(programName, CostForLastExecution);
        }

    }

    @Override
    public void switchFunction(String username, String functionName) {
        usersManager.getUser(username).switchToFunction(functionName);
    }

    @Override
    public void setActiveProgram(String username, String programName) {
        User user = usersManager.getUser(username);
        executionManager.setDebugMode(user, false);
        if (user.hasProgram(programName)) {
            user.setActiveProgram(programName);
            return;
        }
        user.pullSfunctions(sharedProgramsContainer.getAllSFunctions());

        if (sharedProgramsContainer.getAllFunctionNames().contains(programName)) {
            //is a function
            user.activateNewProgram(sharedProgramsContainer.getSFunctions(programName));
        } else {
            //is a program
            user.activateNewProgram(sharedProgramsContainer.getSProgram(programName));
            ;
        }
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

    @Override
    public List<ProgramData> getAllSharedProgramsData() {
        List<ProgramData> programDataList = new ArrayList<>();
        synchronized (sharedProgramsContainer) {
            for (String programName : sharedProgramsContainer.getAllProgramNames()) {
                programDataList.add(sharedProgramsContainer.getSharedProgramData(programName));
            }
        }
        return programDataList;
    }

    @Override
    public List<ProgramData> getAllSharedFunctionsData() {
        List<ProgramData> programDataList = new ArrayList<>();
        synchronized (sharedProgramsContainer) {
            for (String programName : sharedProgramsContainer.getAllFunctionNames()) {
                programDataList.add(sharedProgramsContainer.getSharedProgramData(programName));
            }
        }
        return programDataList;
    }
}