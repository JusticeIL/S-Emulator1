package controller;

import jakarta.xml.bind.JAXBException;
import program.Program;
import program.ProgramData;
import program.Statistics;
import instruction.component.Variable;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Optional;

public interface Controller {
    void loadProgram(String path) throws JAXBException, FileNotFoundException;
    Optional<ProgramData> getProgramData();
    Void Expand(int level);
    Collection<Variable> RunProgram();
    Statistics getStatistics();

}
