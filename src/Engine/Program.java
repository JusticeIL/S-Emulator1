package Engine;

import java.io.FileNotFoundException;
import java.util.*;

import Engine.XMLandJaxB.SInstruction;
import Engine.XMLandJaxB.SInstructionArgument;
import Engine.XMLandJaxB.SInstructions;
import Engine.XMLandJaxB.SProgram;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Map;

public class Program {

    private Instruction currentInstruction;

    private final List<Instruction> instructionList = new ArrayList<Instruction>();
    private final String EXIT_LABEL = "EXIT";
    int currentCommandIndex; // Program Counter
    int cycleCounter;
    Statistics statistics;
    private final Map<String,Variable> Variables = new TreeMap<>();
    private void update() {

    }

    private void executeCurrentCommand() {
        Optional<Label> nextLabel = Optional.ofNullable(currentInstruction.execute());
        nextLabel.ifPresentOrElse(label -> currentInstruction = label.getLabledInstruction(),
                this::getNextInstruction);
    }


    private void getNextInstruction() {
        currentCommandIndex++;
        currentInstruction = instructionList.get(currentCommandIndex);
    }

    public void runProgram(int ...variables) {
        setArguments(variables);
        currentInstruction = instructionList.getFirst();
        while(currentCommandIndex <= instructionList.size()) {
            executeCurrentCommand();
        }
    }

    private void setArguments(int[] variables) {
        int variableCounter = 1;
        for (int variable : variables) {
            String variableName = "x" + variableCounter; //The xs could also not be in order
            Variables.put(variableName, new Variable(variableName, variable));
            variableCounter++;
        }
    }

    public void loadProgram(String filePath) throws FileNotFoundException, JAXBException {
        if (new File(filePath).exists()) {
            JAXBContext jaxbContext = JAXBContext.newInstance(SProgram.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SProgram sProgram = (SProgram) jaxbUnmarshaller.unmarshal(new File(filePath));
            List<SInstruction> sInstructions = sProgram.getSInstructions().getSInstruction();
            InstructionFactory instructionFactory = new InstructionFactory(Variables);
            for (SInstruction sInstr : sInstructions) {
                Instruction newInstruction = instructionFactory.GenerateInstruction(sInstr, instructionList.size());
                instructionList.add(newInstruction);
            }
        }
        else  {
            throw new FileNotFoundException();
        }
    }

    public List<Variable> getVariables() {
        return (List<Variable>)Variables.values();
    }

    public Program(String filePath) throws FileNotFoundException, JAXBException {
        loadProgram(filePath);
        this.statistics = new Statistics();
        this.currentCommandIndex = 0;
        this.cycleCounter = 0;
        this.currentInstruction = instructionList.getFirst();
    }
}