package controller;

import instruction.component.Variable;
import jakarta.xml.bind.JAXBException;
import program.Program;
import program.ProgramData;
import program.Statistics;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SingleProgramController implements Controller{

    Map<Integer, Program> ProgramExpansionsByLevel = new HashMap<>();
    Program activeProgram;
    Statistics statistics;

    @Override
    public void loadProgram(String path) throws FileNotFoundException, JAXBException {
        try {
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
    public void Expand(int level) {
        if(level > activeProgram.getMaxProgramLevel()){
            throw new IllegalArgumentException("Level exceeds maximum program level of " + activeProgram.getMaxProgramLevel());
        }
        if(ProgramExpansionsByLevel.containsKey(level)) {
            activeProgram = ProgramExpansionsByLevel.get(level);
        }else{
            Program expandedProgram = ProgramExpansionsByLevel.get(0).expand(level);
            if(expandedProgram != null) {
                ProgramExpansionsByLevel.put(level, expandedProgram);
                activeProgram = expandedProgram;
            } else {
                return;
            }
        }
    }

    @Override
    public Collection<Variable> runProgram(int... args) {
        activeProgram.runProgram(args);
        return activeProgram.getVariables();
    }

    @Override
    public Statistics getStatistics() {
        return statistics;
    }
}
