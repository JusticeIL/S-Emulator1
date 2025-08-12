package Engine;

import java.util.List;

import Engine.XMLandJaxB.SInstruction;
import Engine.XMLandJaxB.SProgram;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;

public class Program {

    private Instruction currentInstruction;
    private List<Instruction> instructionList;
    private String EXIT_LABEL = "EXIT";
    int PC; // Program Counter
    int cycleCounter;
    Statistics statistics;

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

    public static void loadProgram(String filePath) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SProgram.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SProgram other = (SProgram) jaxbUnmarshaller.unmarshal(new File(filePath));
            System.out.println("Program loaded successfully: " + other.getName());
            System.out.println("Program loaded this:");
            for (SInstruction inst : other.getSInstructions().getSInstruction()) {
                System.out.println(inst.getSVariable() + " - " + inst.getName() + " - " + inst.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
