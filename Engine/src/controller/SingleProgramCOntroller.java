package controller;

import instruction.component.Variable;
import jakarta.xml.bind.JAXBException;
import program.Program;
import program.ProgramData;
import program.Statistics;

import java.io.FileNotFoundException;
import java.util.Collection;

public class SingleProgramCOntroller implements Controller{

    Program program;
    Statistics statistics;

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public ControllerResponse loadProgram(String path){
        try {
            program.loadProgram(path);
            return new ControllerResponse();
        } catch (FileNotFoundException e) {
            return new ControllerResponse("File not found");
        } catch (JAXBException e) {
            return new ControllerResponse("File invalid");
        }
    }

    @Override
    public ProgramData getProgramData(Program program) {
        return new ProgramData(program);
    }

    @Override
    public Void Expand(int level) {
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
