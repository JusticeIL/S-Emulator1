package Engine;

import java.util.*;

import Engine.XMLandJaxB.SInstruction;
import Engine.XMLandJaxB.SInstructionArgument;
import Engine.XMLandJaxB.SInstructions;
import Engine.XMLandJaxB.SProgram;
import jakarta.xml.bind.JAXBContext;
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


    public void loadProgram(String filePath) {
        try {
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
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Variable> getVariables() {
        return (List<Variable>)Variables.values();
    }
}
