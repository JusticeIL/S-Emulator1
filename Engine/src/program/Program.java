package program;

import java.io.FileNotFoundException;
import java.util.*;

import XMLandJaxB.SInstruction;
import XMLandJaxB.SProgram;
import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.InstructionFactory;
import instruction.component.Label;
import instruction.component.Variable;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Program {

    private Instruction currentInstruction;
    private String programName;
    private final List<Instruction> instructionList = new LinkedList<Instruction>();
    int currentCommandIndex; // Program Counter
    int cycleCounter;
    private int runCounter;
    private final int currentProgramLevel;
    private int maxProgramLevel = 0; // TODO: Implement this in the future
    private Statistics statistics;
    private final Map<String, Variable> Variables = new TreeMap<>();
    static public final Label EMPTY_LABEL = new Label("     ");
    static public final Label EXIT_LABEL = new Label("EXIT");
    private boolean wasExpanded = false;
    private Program expandedProgram = null;

    public Set<Label> getLabels() {
        return Labels.keySet();
    }

    private final Map<Label, Instruction> Labels = new HashMap<>();

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
            currentCommandIndex = currentInstruction.getNumber() - 1;
        }

    }

    private int calculateMaxProgramLevel() {
        // Calculate the maximum program level based on the instructions
        return instructionList.stream()
                .mapToInt(Instruction::getLevel)
                .max()
                .orElse(0);
    }

    public void revertExpansion() {
        for (Instruction instruction : instructionList) {
            instruction.revertExpansion();
        }
    }

    public List<String> getExpandedProgramStringRepresentation() {
        List<String> result = new ArrayList<>();
        for (instruction.Instruction instruction : instructionList) {
            result.addAll(instruction.getExpandedStringRepresentation());
        }
        return result;
    }

    public Program expand(int level) {
        if (wasExpanded) {
            return this.expandedProgram.expand(level-1);
        }else if(level == 0){
            return this;
        }
        else {
            List<Instruction> expandedInstructions = new ArrayList<>();
            Map<Label, Instruction> expandedLabels = new HashMap<>(Labels);
            Set<Variable> expandedVariables = new HashSet<>(Variables.values());

            instructionList.forEach(instruction -> {
                ExpandedSyntheticInstructionArguments singleExpandedInstruction = instruction.generateExpandedInstructions();
                if (singleExpandedInstruction != null) {
                    expandedVariables.addAll(singleExpandedInstruction.getVariables());
                    expandedInstructions.addAll(singleExpandedInstruction.getInstructions());
                    singleExpandedInstruction.getLabels().forEach((label, instr) -> {
                        if (!Labels.containsKey(label)) {
                            expandedLabels.put(label, instr);
                        }
                    });
                }
            });
            IntStream.range(1, expandedInstructions.size() + 1).forEach(i -> expandedInstructions.get(i - 1).setNumber(i));
            ExpandedSyntheticInstructionArguments expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables, expandedLabels, expandedInstructions);

            wasExpanded = true;
            this.expandedProgram = new Program(this, expandedInstruction);
            return this.expandedProgram.expand(level-1);
        }
    }

    // In Program.java
    public void runProgram(int ...variables) {
        setUpNewRun();
        setArguments(variables);
        List<Variable> xVariables = Variables.entrySet().stream()
                .filter(entry -> entry.getKey().contains("x"))
                .map(Map.Entry::getValue)
                .toList();

        // Delegate execution to InstructionExecutioner
        InstructionExecutioner.executeInstructions(instructionList, Labels);

        int yValue = Variables.get("y").getValue();
        Run currentRun = new Run(runCounter, currentProgramLevel, xVariables, yValue, cycleCounter);
        statistics.addRunToHistory(currentRun);
        runCounter++;
    }

    public Program(Program baseProgram, ExpandedSyntheticInstructionArguments newInstructions) {
        // Copy statistics and program name from the base program
        this.statistics = baseProgram.getStatistics();
        this.programName = baseProgram.getProgramName();
        this.instructionList.addAll(newInstructions.getInstructions());

        // Copy variables and labels from the base program
        this.Variables.putAll(newInstructions.getVariables().stream().
                collect(Collectors.toMap(Variable::getName, v -> v)));

        this.Labels.putAll(newInstructions.getLabels());

        // Set up initial state
        this.currentCommandIndex = 0;
        this.cycleCounter = 0;
        this.runCounter = baseProgram.runCounter;
        this.currentProgramLevel = baseProgram.currentProgramLevel + 1;
        this.maxProgramLevel = Math.max(this.currentProgramLevel, calculateMaxProgramLevel());

        // Set the current instruction if the list is not empty
        if (!instructionList.isEmpty()) {
            this.currentInstruction = instructionList.getFirst();
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
        variableCounter--;
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
        this.currentCommandIndex = 0;
        this.cycleCounter = 0;
    }

    public Program(String filePath) throws FileNotFoundException, JAXBException {
        loadProgram(filePath);

        this.statistics = new Statistics();
        this.currentCommandIndex = 0;
        this.cycleCounter = 0;
        this.currentInstruction = instructionList.getFirst();
        this.runCounter = 1;
        this.currentProgramLevel = 0;
        this.maxProgramLevel = calculateMaxProgramLevel();
    }

    public int getProgramCycles() {
        return cycleCounter;
    }

    public int getMaxProgramLevel() {
        return maxProgramLevel;
    }

    public Statistics getStatistics() {
        return statistics;
    }


}