package controller;

import jakarta.xml.bind.JAXBException;
import program.Program;
import program.ProgramExecutioner;
import dto.ProgramData;
import dto.VariableDTO;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;

public class SingleProgramController implements Model, Serializable {

    private Program activeProgram;
    private Map<Integer, Program> activeProgramExpansionsByLevel;
    private boolean isCurrentlyInDebugMode = false;
    private final Map<String,Map<Integer,Program>> programsAndFunctionsByName = new HashMap<>();
    private final ProgramExecutioner programExecutioner = new ProgramExecutioner();

    @Override
    public void loadProgram(String path) throws FileNotFoundException, JAXBException {
        try {
            programsAndFunctionsByName.clear();
            activeProgramExpansionsByLevel= new HashMap<>();
            this.activeProgram = new Program(path);
            this.activeProgramExpansionsByLevel.put(0, activeProgram);
            this.programsAndFunctionsByName.put(activeProgram.getProgramName(), activeProgramExpansionsByLevel);

            activeProgram.getFunctions().forEach(function -> {
                HashMap<Integer,Program> functionExpansionMap = new HashMap<>();
                functionExpansionMap.put(0,function);
                programsAndFunctionsByName.put(function.getProgramName(), functionExpansionMap);
            });

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found at path: " + path);
        } catch (JAXBException e) {
            throw new JAXBException("Error parsing XML file at path: " + path);
        }
    }

    @Override
    public boolean isProgramLoaded() {
        return activeProgram != null;
    }

    @Override
    public Optional<ProgramData> getProgramData() {
        return Optional.ofNullable(activeProgram)
                .map(ProgramData::new);
    }

    @Override
    public void Expand(int level) {
        int maxLevel = activeProgramExpansionsByLevel.get(0).getMaxProgramLevel();
        if (level > maxLevel) {
            throw new IllegalArgumentException("Level exceeds maximum program level of " + maxLevel);
        }
        else if (level < 0) {
            throw new IllegalArgumentException("Level is a negative number! the level number should be between 0 and " + activeProgram.getMaxProgramLevel());
        }
        if (activeProgramExpansionsByLevel.containsKey(level)) {
            activeProgram = activeProgramExpansionsByLevel.get(level);
        } else {
            Program expandedProgram = activeProgramExpansionsByLevel.get(0).expand(level);
            if (expandedProgram != null) {
                activeProgramExpansionsByLevel.put(level, expandedProgram);
                activeProgram = expandedProgram;
            }
        }
    }

    @Override
    public void runProgram(Set<VariableDTO> args) {
        programExecutioner.setMainExecutioner();
        programExecutioner.setProgram(activeProgram);
        programExecutioner.executeProgram(args);
    }

    @Override
    public void startDebug(Set<VariableDTO> args,Set<Integer> breakpoints) {
        programExecutioner.setDebugMode(true);
        programExecutioner.setProgram(activeProgram);
        programExecutioner.setUpDebugRun(args, breakpoints);
        isCurrentlyInDebugMode = true;
    }

    @Override
    public void addBreakpoint(int lineNumber) {
        programExecutioner.addBreakpoint(lineNumber);
    }

    @Override
    public void removeBreakpoint(int lineNumber) {
        programExecutioner.removeBreakpoint(lineNumber);
    }

    @Override
    public void stepOver() {
        if(isCurrentlyInDebugMode) {
            programExecutioner.stepOver();
        }
    }

    @Override
    public void stopDebug() {
        if(isCurrentlyInDebugMode) {
            programExecutioner.stopDebug();
            programExecutioner.setDebugMode(false);
            isCurrentlyInDebugMode = false;
        }
    }

    @Override
    public void resumeDebug() {
        programExecutioner.resumeDebug();
    }

    @Override
    public void switchFunction(String functionName) {

        if (programsAndFunctionsByName.containsKey(functionName)) { // Case: the function name belongs to the main program
            activeProgramExpansionsByLevel = programsAndFunctionsByName.get(functionName);
            activeProgram = activeProgramExpansionsByLevel.get(0);
            return;
        }

        // Case: the function name belongs to a function
        activeProgram.getFunctions().stream()
                .filter(function -> function.getUserString().equals(functionName))
                .findFirst()
                .ifPresent(function -> {
                    activeProgramExpansionsByLevel = programsAndFunctionsByName.get(function.getProgramName());
                    activeProgram = activeProgramExpansionsByLevel.get(0);
                });
    }
}