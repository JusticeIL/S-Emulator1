package controller;

import XMLandJaxB.SProgram;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import program.Program;
import program.ProgramExecutioner;
import dto.ProgramData;
import dto.VariableDTO;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

public class MultiUserController implements MultiUserModel, Serializable {


    private final Map<String,Map<String,Map<Integer,Program>>> usersToPrograms = new HashMap<>();
    private final Map<String, SProgram> loadedPrograms = new HashMap<>();
    private final Map<String,Program> activeProgramsByUser = new HashMap<>();
    private final Map<String, Boolean> isCurrentlyInDebugMode = new HashMap<>();
    private final Map<String, ProgramExecutioner> programExecutionersByUser = new HashMap<>();
    private Map <String, Map<Integer, Program>> activeProgramExpansionsByLevelByUser = new HashMap<>();

    @Override
    public void loadProgram(String username, InputStream path) throws FileNotFoundException, JAXBException {
        try {
            //TODO: if program in loadedPrograms, load from there
            usersToPrograms.putIfAbsent(username, new HashMap<>());
            isCurrentlyInDebugMode.putIfAbsent(username, false);
            programExecutionersByUser.putIfAbsent(username, new ProgramExecutioner());
            SProgram sProgram;

            synchronized (loadedPrograms) {

                JAXBContext jaxbContext = JAXBContext.newInstance(SProgram.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                sProgram = (SProgram) jaxbUnmarshaller.unmarshal(path);

                loadedPrograms.putIfAbsent(sProgram.getName(), sProgram);
            }

            usersToPrograms.get(username).putIfAbsent(sProgram.getName(), new HashMap<>());
            usersToPrograms.get(username).get(sProgram.getName()).putIfAbsent(0, new Program(sProgram));
            activeProgramsByUser.put(username, usersToPrograms.get(username).get(sProgram.getName()).get(0));
            activeProgramExpansionsByLevelByUser.put(username, usersToPrograms.get(username).get(sProgram.getName()));
            Program activeProgram = activeProgramsByUser.get(username);
            activeProgram.getFunctions().forEach(function -> {
                HashMap<Integer,Program> functionExpansionMap = new HashMap<>();
                functionExpansionMap.put(0,function);
                usersToPrograms.get(username).put(function.getProgramName(), functionExpansionMap);
            });

        } catch (JAXBException e) {
            throw new JAXBException("Error parsing XML file at path: " + path);
        }
    }//TODO: handle loading the same program twice

    @Override
    public boolean isProgramLoaded(String username) {
        return usersToPrograms.containsKey(username)&&!usersToPrograms.get(username).isEmpty();
    }

    @Override
    public Optional<ProgramData> getProgramData(String username) {
        return Optional.ofNullable(activeProgramsByUser.get(username))
                .map(ProgramData::new);
    }

    @Override
    public void Expand(String username, int level) {
        Program activeProgram = activeProgramsByUser.get(username);
        Map<Integer, Program> activeProgramExpansionsByLevel = usersToPrograms.get(username).get(activeProgram.getProgramName());
        int maxLevel = activeProgramExpansionsByLevel.get(0).getMaxProgramLevel();
        if(level > maxLevel) {
            throw new IllegalArgumentException("Level exceeds maximum program level of " + maxLevel);
        }
        else if (level < 0) {
            throw new IllegalArgumentException("Level is a negative number! the level number should be between 0 and " + activeProgram.getMaxProgramLevel());
        }
        if(activeProgramExpansionsByLevel.containsKey(level)) {
            activeProgramsByUser.put(username,activeProgramExpansionsByLevel.get(level));
        } else {
            Program expandedProgram = activeProgramExpansionsByLevel.get(0).expand(level);
            if(expandedProgram != null) {
                activeProgramExpansionsByLevel.put(level, expandedProgram);
                activeProgramsByUser.put(username,expandedProgram);
            }
        }
    }

    @Override
    public void runProgram(String username, Set<VariableDTO> args) {
        Program activeProgram = activeProgramsByUser.get(username);
        ProgramExecutioner programExecutioner = programExecutionersByUser.get(username);
        programExecutioner.setMainExecutioner();
        programExecutioner.setProgram(activeProgram);
        programExecutioner.executeProgram(args);
    }

    @Override
    public void startDebug(String username, Set<VariableDTO> args,Set<Integer> breakpoints) {
        Program activeProgram = activeProgramsByUser.get(username);
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
        Program activeProgram = activeProgramsByUser.get(username);
        Map<String, Map<Integer, Program>> programsAndFunctionsByName = usersToPrograms.get(username);


        if (programsAndFunctionsByName.containsKey(functionName)) { // Case: the function name belongs to the main program
            activeProgramExpansionsByLevelByUser.put(username,programsAndFunctionsByName.get(functionName)) ;
            activeProgramsByUser.put(username,activeProgramExpansionsByLevelByUser.get(username).get(0));
            return;
        }

        // Case: the function name belongs to a function
        activeProgram.getFunctions().stream()
                .filter(function -> function.getUserString().equals(functionName))
                .findFirst()
                .ifPresent(function -> {
                    activeProgramExpansionsByLevelByUser.put(username,programsAndFunctionsByName.get(function.getProgramName()));
                    activeProgramsByUser.put(username, activeProgramExpansionsByLevelByUser.get(username).get(0));
                });
    }
}