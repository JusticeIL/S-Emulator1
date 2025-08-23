package controller;

import instruction.component.Variable;
import jakarta.xml.bind.JAXBException;
import program.Program;
import program.ProgramData;
import program.Statistics;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Optional;

public class SingleProgramController implements Controller{

    Program program;
    Statistics statistics;

    @Override
    public void loadProgram(String path) throws FileNotFoundException, JAXBException {
        try {
            this.program = new Program(path);
            this.statistics = new Statistics();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found at path: " + path);
        } catch (JAXBException e) {
            throw new JAXBException("Error parsing XML file at path: " + path);
        }
    }

    @Override
    public Optional<ProgramData> getProgramData() {
        return Optional.ofNullable(program)
                .map(ProgramData::new);
    }

    @Override
    public Void Expand(int level) {
        program.expand(level);
        return null;
    }



    @Override
    public Collection<Variable> RunProgram() {
        program.runProgram();
        return program.getVariables();
    }

    @Override
    public Statistics getStatistics() {
        return statistics;
    }
}
