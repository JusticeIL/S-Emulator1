package controller;

import jakarta.xml.bind.JAXBException;
import program.Program;
import program.ProgramData;
import program.Statistics;
import instruction.component.Variable;

import java.io.FileNotFoundException;
import java.util.List;

public interface Controller {
    Program getProgram();
    ControllerResponse loadProgram(String path) throws JAXBException, FileNotFoundException;
    ProgramData getProgramData(Program program);
    Void Expand(int level);
    List<Variable> RunProgram();
    Statistics getStatistics();

}
