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
    private final Set<String> usedXVariableNames;
    int currentCommandIndex; // Program Counter
    int cycleCounter;
    private final int runCounter;
    private final int currentProgramLevel;
    private final int maxProgramLevel;
    private final Statistics statistics;
    private final Map<String, Variable> Variables = new TreeMap<>();
    static public final Label EMPTY_LABEL = new Label("    ");
    static public final Label EXIT_LABEL = new Label("EXIT");

    private boolean wasExpanded = false;
    private Program expandedProgram = null;
    private final List<Instruction> runtimeExecutedInstructions = new ArrayList<>();


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



    private int calculateMaxProgramLevel() {
        // Calculate the maximum program level based on the instructions
        return instructionList.stream()
                .mapToInt(Instruction::getLevel)
                .max()
                .orElse(0);
    }

    public List<String> getExpandedProgramStringRepresentation() {
        List<String> result = new ArrayList<>();
        for (instruction.Instruction instruction : instructionList) {
            result.addAll(instruction.getExpandedStringRepresentation());
        }
        return result;
    }

    public Program expand(int level) {
        if (level == 0) {
            return this;
        } else if (wasExpanded) {
            return this.expandedProgram.expand(level - 1);
        } else {
            List<Instruction> expandedInstructions = new ArrayList<>();
            Map<Label, Instruction> expandedLabels = new HashMap<>(Labels);
            Set<Variable> expandedVariables = new HashSet<>(Variables.values());

            instructionList.forEach(instruction -> {
                ExpandedSyntheticInstructionArguments singleExpandedInstruction = instruction.generateExpandedInstructions();
                if (singleExpandedInstruction != null) {
                    expandedVariables.addAll(singleExpandedInstruction.getVariables());
                    expandedInstructions.addAll(singleExpandedInstruction.getInstructions());
                    expandedLabels.putAll(singleExpandedInstruction.getLabels());
                }
            });
            IntStream.range(1, expandedInstructions.size() + 1).forEach(i -> expandedInstructions.get(i - 1).setNumber(i));
            ExpandedSyntheticInstructionArguments expandedInstruction = new ExpandedSyntheticInstructionArguments(expandedVariables, expandedLabels, expandedInstructions);

            wasExpanded = true;
            this.expandedProgram = new Program(this, expandedInstruction);
            return this.expandedProgram.expand(level - 1);
        }
    }

    public void runProgram(int ...variables) {
        setUpNewRun();
        setArguments(variables);
        Map<String,Integer> xVariables = new HashMap<>();
        Variables.entrySet().stream()
                .filter(entry -> entry.getKey().contains("x"))
                .map(Map.Entry::getValue)
                .forEach(v -> xVariables.put(v.getName(), v.getValue()));


        Label nextLabel = null;
        int currentIndex = 0;
        Instruction currentInstruction = instructionList.get(currentIndex);


        while (currentIndex < instructionList.size()) {
            runtimeExecutedInstructions.add(currentInstruction);
            nextLabel = currentInstruction.execute();
            cycleCounter += currentInstruction.getCycles();

            if (nextLabel.equals(Program.EMPTY_LABEL)) {
                currentIndex++;
            } else if (nextLabel.equals(Program.EXIT_LABEL)) {
                currentIndex = instructionList.size(); // Exit the loop
            } else {
                currentInstruction = Labels.get(nextLabel);
                currentIndex = currentInstruction.getNumber() - 1;
            }

            if (currentIndex < instructionList.size()) {
                currentInstruction = instructionList.get(currentIndex);
            }
        }

        int yValue = Variables.get("y").getValue();
        statistics.addRunToHistory(currentProgramLevel, xVariables, yValue, cycleCounter);
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
        this.maxProgramLevel = baseProgram.maxProgramLevel-1;
        this.usedXVariableNames = baseProgram.usedXVariableNames;

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
            boolean containsExit = false;
            Label.saveHighestUnusedLabelNumber();
            Label.resetHighestUnusedLabelNumber();

            Variable.saveHighestUnusedZId();
            Variable.resetZIdCounter();

            for (SInstruction sInstr : sInstructions) {
                Instruction newInstruction = instructionFactory.GenerateInstruction(sInstr, instructionCounter);
                instructionList.add(newInstruction);
                if (!newInstruction.getLabel().equals(EMPTY_LABEL)) { // Case: add label iff it is not empty
                    Labels.put(newInstruction.getLabel(), newInstruction);
                }
                if(newInstruction.getDestinationLabel().equals(EXIT_LABEL)){
                    containsExit = true;
                }
                instructionCounter++;
            }
            if(containsExit){
                Instruction ExitInstruction = instructionFactory.GenerateExitInstruction(instructionList.size());
                Labels.put(EXIT_LABEL, ExitInstruction); // Special case: EXIT label
            }
            // Load program name
            programName = sProgram.getName();
            Set<Label> missingLabels = instructionFactory.getMissingLabels();
            if (!missingLabels.isEmpty()) {
                Label.loadHighestUnusedLabelNumber();
                Variable.loadHighestUnusedZId();
                throw new IllegalArgumentException("The following labels are used but not defined: " + missingLabels);
            }

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
        List<String> keysToRemove = Variables.keySet().stream()
                .filter(v -> v.startsWith("x"))
                .filter(v-> !usedXVariableNames.contains(v))
                .toList();
        for (String key : keysToRemove) {
            Variables.remove(key);
        }
        for (Variable variable : Variables.values()) {
            variable.setValue(0);
        }
        this.currentCommandIndex = 0;
        this.cycleCounter = 0;
        runtimeExecutedInstructions.clear(); ;
    }

    public List<Instruction> getRuntimeExecutedInstructions() {
        return runtimeExecutedInstructions;
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
        this.usedXVariableNames = Variables.keySet().stream()
                .filter(name -> name.startsWith("x"))
                .collect(Collectors.toSet());
    }

    public Set<String> getUsedXVariableNames() {
        return usedXVariableNames;
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