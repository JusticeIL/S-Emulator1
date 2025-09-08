package controller;

import jakarta.xml.bind.JAXBException;
import program.data.ProgramData;
import instruction.component.Variable;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Optional;

public interface Model {
    void loadProgram(String path) throws JAXBException, FileNotFoundException;
    boolean isProgramLoaded();
    Optional<ProgramData> getProgramData();
    void Expand(int level) throws IllegalArgumentException;
    Collection<Variable> runProgram(int ... args);
}
