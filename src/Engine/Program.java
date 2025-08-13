package Engine;

import java.util.ArrayList;
import java.util.List;

import Engine.XMLandJaxB.SInstruction;
import Engine.XMLandJaxB.SInstructionArgument;
import Engine.XMLandJaxB.SInstructions;
import Engine.XMLandJaxB.SProgram;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class Program {

    private Instruction currentInstruction;
    private final List<Instruction> instructionList = new ArrayList<Instruction>();
    private String EXIT_LABEL = "EXIT";
    int currentCommandIndex; // Program Counter
    int cycleCounter;
    Statistics statistics;
    private final Map<String,Variable> Variables = new TreeMap<>();
    private void update() {

    }

    public void executeCurrentCommand() {
        String nextLabel = currentInstruction.execute();
        if (nextLabel.equals("")) {
            // Case: no label
        } else if (nextLabel.equals(EXIT_LABEL)) {
            // Case: exit command
        } else {
            // The rest
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
}
