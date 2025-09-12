package controller;

import instruction.Instruction;
import instruction.component.Variable;
import jakarta.xml.bind.JAXBException;
import program.Program;
import program.ProgramExecutioner;
import program.data.ProgramData;
import program.Statistics;
import program.data.VariableDTO;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;

public class SingleProgramController implements Model, Serializable {

    private Program activeProgram;
    private final Map<Integer, Program> ProgramExpansionsByLevel = new HashMap<>();
    private Statistics statistics;
    private final ProgramExecutioner programExecutioner = new ProgramExecutioner();
    private boolean isCurrentlyInDebugMode = false;

    @Override
    public void loadProgram(String path) throws FileNotFoundException, JAXBException {
        try {
            ProgramExpansionsByLevel.clear();
            this.activeProgram = new Program(path);
            this.ProgramExpansionsByLevel.put(0, activeProgram);
            this.statistics = new Statistics();
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
    public void StopDebug() {
        if(isCurrentlyInDebugMode) {
            programExecutioner.setDebugMode(false);
            isCurrentlyInDebugMode = false;
        }
    }

    @Override
    public void Expand(int level) {
        int maxLevel = ProgramExpansionsByLevel.get(0).getMaxProgramLevel();
        if(level > maxLevel) {
            throw new IllegalArgumentException("Level exceeds maximum program level of " + maxLevel);
        }
        else if (level < 0) {
            throw new IllegalArgumentException("Level is a negative number! the level number should be between 0 and " + activeProgram.getMaxProgramLevel());
        }
        if(ProgramExpansionsByLevel.containsKey(level)) {
            activeProgram = ProgramExpansionsByLevel.get(level);
        } else {
            Program expandedProgram = ProgramExpansionsByLevel.get(0).expand(level);
            if(expandedProgram != null) {
                ProgramExpansionsByLevel.put(level, expandedProgram);
                activeProgram = expandedProgram;
            }
        }
    }



    @Override
    public void runProgram(Set<VariableDTO> args) {
        programExecutioner.setProgram(activeProgram);
        programExecutioner.executeProgram(args);
    }

    @Override
    public void stepOver() {
        if(isCurrentlyInDebugMode) {
            programExecutioner.stepOver();
        }
    }

    @Override
    public void startDebug(Set<VariableDTO> args) {
        programExecutioner.setDebugMode(true);
        programExecutioner.setProgram(activeProgram);
        programExecutioner.setUpDebugRun(args);
        isCurrentlyInDebugMode = true;
    }
}