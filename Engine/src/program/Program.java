package program;

import java.io.FileNotFoundException;
import java.util.*;

import XMLandJaxB.SInstruction;
import XMLandJaxB.SProgram;
import instruction.Instruction;
import instruction.InstructionFactory;
import instruction.component.Label;
import instruction.component.Variable;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Map;

public class Program {

    private Instruction currentInstruction;
    private String programName;
    private final List<Instruction> instructionList = new LinkedList<Instruction>();
    int currentCommandIndex; // Program Counter
    int cycleCounter;
    Statistics statistics;
    private final Map<String, Variable> Variables = new TreeMap<>();
    static public final Label EMPTY_LABEL =  new Label("     ");
    static protected final Label EXIT_LABEL = new Label("EXIT");

    public Set<Label> getLabels() {
        return Labels.keySet();
    }

    private final Map<Label,Instruction> Labels = new HashMap<>();

    public List<Instruction> getInstructionList() {
        return instructionList;
    }

    public String getProgramName() {
        return programName;
    }

    private void executeCurrentCommand() {
        Label nextLabel = currentInstruction.execute();

        if (nextLabel.equals(EMPTY_LABEL)) {
            currentCommandIndex++;
            if (currentCommandIndex < instructionList.size()) {
                currentInstruction = instructionList.get(currentCommandIndex);
            }
        } else if (nextLabel.equals(EXIT_LABEL)) {
            currentCommandIndex = instructionList.size() + 1;
        } else { // Case: GOTO Label
            currentInstruction = Labels.get(nextLabel);
            currentCommandIndex = currentInstruction.getNumber();
        }

    }


    private void getNextInstruction() {
        currentCommandIndex++;
        currentInstruction = instructionList.get(currentCommandIndex);
    }

    public void runProgram(int ...variables) {
        setUpNewRun();
        setArguments(variables);
        currentInstruction = instructionList.getFirst();
        while(currentCommandIndex < instructionList.size()) {
            executeCurrentCommand();
        }
    }

    private void setArguments(int[] arguments) {
        int variableCounter = 1;
        for (int variable : arguments) {
            String variableName = "x" + variableCounter; //The xs could also not be in order
            if(Variables.containsKey(variableName)) {
                Variables.get(variableName).setValue(variable);
            }else{
                Variables.put(variableName, new Variable(variableName, variable));
            }
            variableCounter++;
        }
    }

    public void loadProgram(String filePath) throws FileNotFoundException, JAXBException {
        if (new File(filePath).exists()) {
            // Load JAXB
            JAXBContext jaxbContext = JAXBContext.newInstance(SProgram.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SProgram sProgram = (SProgram) jaxbUnmarshaller.unmarshal(new File(filePath));
            List<SInstruction> sInstructions = sProgram.getSInstructions().getSInstruction();
            // Load instructions
            InstructionFactory instructionFactory = new InstructionFactory(Variables);
            int instructionCounter = 1;
            for (SInstruction sInstr : sInstructions) {
                Instruction newInstruction = instructionFactory.GenerateInstruction(sInstr, instructionCounter);
                instructionList.add(newInstruction);
                if (!newInstruction.getLabel().equals(EMPTY_LABEL)) { // Case: add label iff it is not empty
                    Labels.put(newInstruction.getLabel(), newInstruction);
                }
                instructionCounter++;
            }
            // Load program name
            programName = sProgram.getName();
        }
        else  {
            throw new FileNotFoundException();
        }
    }

    public Collection<Variable> getVariables() {
        return Variables.values();
    }

    private void setUpNewRun(){
        if(Variables.containsKey("y")) {
            Variables.get("y").setValue(0);
        }else{
            Variables.put("y", new Variable("y", 0));
        }
        for (Variable variable : Variables.values()) {
            variable.setValue(0);
        }
        currentCommandIndex = 0;
    }

    public Program(String filePath) throws FileNotFoundException, JAXBException {
        loadProgram(filePath);
        this.statistics = new Statistics();
        this.currentCommandIndex = 0;
        this.cycleCounter = calculateProgramCycles();
        this.currentInstruction = instructionList.getFirst();
    }

    private int calculateProgramCycles() {
        return instructionList.stream().
                mapToInt(Instruction::getCycles)
                .sum();
    }

    public int getProgramCycles() {
        return cycleCounter;
    }
}