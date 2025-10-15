package program;

import XMLandJaxB.*;
import instruction.ExpandedSyntheticInstructionArguments;
import instruction.Instruction;
import instruction.InstructionFactory;
import instruction.component.Label;
import instruction.component.LabelFactory;
import instruction.component.Variable;
import instruction.component.VariableFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import program.function.Function;
import program.function.FunctionsContainer;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;

public class Program implements Serializable {

    private final FunctionsContainer functionsContainer;
    private Instruction currentInstruction;
    private int cycleCounter;
    private Program expandedProgram = null;
    private String programName;
    private boolean wasExpanded = false;
    private final int currentProgramLevel;
    private final List<Instruction> instructionList = new LinkedList<>();
    private final Map<Label, Instruction> Labels = new HashMap<>();
    private final int maxProgramLevel;
    private final int runCounter;
    private final List<Instruction> runtimeExecutedInstructions = new ArrayList<>();
    private final Statistics statistics;
    private final Set<String> usedXVariableNames;
    private final Map<String, Variable> Variables = new TreeMap<>();
    static public final Label EMPTY_LABEL = new Label("    ");
    static public final Label EXIT_LABEL = new Label("EXIT");
    private final LabelFactory labelFactory;
    private final VariableFactory variableFactory;
    private int nextInstructionIdForDebug;
    private boolean isInDebugMode = false;

    public Program(String filePath) throws FileNotFoundException, JAXBException {
        this.labelFactory = new LabelFactory();
        this.variableFactory = new VariableFactory();
        this.functionsContainer = new FunctionsContainer();
        loadProgram(filePath);
        this.statistics = new Statistics();
        this.cycleCounter = 0;
        this.currentInstruction = instructionList.getFirst();
        this.runCounter = 1;
        this.currentProgramLevel = 0;
        this.maxProgramLevel = calculateMaxProgramLevel();
        this.usedXVariableNames = Variables.keySet().stream()
                .filter(name -> name.startsWith("x"))
                .collect(Collectors.toSet());
    }

    public Program(Program baseProgram, ExpandedSyntheticInstructionArguments newInstructions) {
        // Copy statistics and program name from the base program
        this.statistics = baseProgram.getStatistics();
        this.programName = baseProgram.getProgramName();
        this.labelFactory = baseProgram.labelFactory;
        this.variableFactory = baseProgram.variableFactory;
        this.instructionList.addAll(newInstructions.getInstructions());
        this.Variables.putAll(baseProgram.Variables);
        this.functionsContainer = baseProgram.functionsContainer;

        // Copy variables and labels from the base program
        this.Variables.putAll(newInstructions.getVariables().stream().
                collect(Collectors.toMap(Variable::getName, v -> v)));

        this.Labels.putAll(newInstructions.getLabels());

        // Set up initial state
        this.cycleCounter = 0;
        this.runCounter = baseProgram.runCounter;
        this.currentProgramLevel = baseProgram.currentProgramLevel + 1;
        this.maxProgramLevel = baseProgram.maxProgramLevel - 1;
        this.usedXVariableNames = baseProgram.usedXVariableNames;

        // Set the current instruction if the list is not empty
        if (!instructionList.isEmpty()) {
            this.currentInstruction = instructionList.getFirst();
        }
    }

    public Program(SInstructions sInstructions, String programName, FunctionsContainer functionsContainer) throws FileNotFoundException {
        this.labelFactory = new LabelFactory();
        this.variableFactory = new VariableFactory();
        this.functionsContainer = functionsContainer;

        InstructionFactory instructionFactory = new InstructionFactory(Variables, labelFactory, variableFactory, functionsContainer);
        int instructionCounter = 1;
        boolean containsExit = false;

        for (SInstruction sInstr : sInstructions.getSInstruction()) {
            Instruction newInstruction = instructionFactory.GenerateInstruction(sInstr, instructionCounter);
            instructionList.add(newInstruction);
            if (!newInstruction.getLabel().equals(EMPTY_LABEL)) { // Case: add label iff it is not empty
                Labels.put(newInstruction.getLabel(), newInstruction);
            }
            if (newInstruction.getDestinationLabel().equals(EXIT_LABEL)) {
                containsExit = true;
            }
            instructionCounter++;
        }
        if (containsExit) {
            Instruction ExitInstruction = instructionFactory.GenerateExitInstruction(instructionList.size());
            Labels.put(EXIT_LABEL, ExitInstruction); // Special case: EXIT label
        }
        // Load program name
        this.programName = programName;
        Set<Label> missingLabels = instructionFactory.getMissingLabels();

        //this is for functions (their set is empty)
        this.statistics = new Statistics();
        this.cycleCounter = 0;
        this.currentInstruction = instructionList.getFirst();
        this.runCounter = 1;
        this.currentProgramLevel = 0;
        this.maxProgramLevel = calculateMaxProgramLevel();
        this.usedXVariableNames = Variables.keySet().stream()
                .filter(name -> name.startsWith("x"))
                .collect(Collectors.toSet());
        if (!missingLabels.isEmpty()) {
            throw new IllegalArgumentException("The following labels are used but not defined: " + missingLabels);
        }
    }

    public Program(SProgram sProgram) {
        this.labelFactory = new LabelFactory();
        this.variableFactory = new VariableFactory();
        this.functionsContainer = new FunctionsContainer();

        List<SInstruction> sInstructions = sProgram.getSInstructions().getSInstruction();
        Optional<SFunctions> sFunctionsOpt = Optional.ofNullable(sProgram.getSFunctions());
        sFunctionsOpt.ifPresent(sFunctions -> {
            functionsContainer.setup(sFunctions.getSFunction());
            functionsContainer.getFunctionNames().forEach(functionName -> {
                try {
                    functionsContainer.tryGetFunction(functionName);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            InstructionFactory instructionFactory = new InstructionFactory(Variables, labelFactory, variableFactory, functionsContainer);
            int instructionCounter = 1;
            boolean containsExit = false;

            for (SInstruction sInstr : sInstructions) {
                Instruction newInstruction = instructionFactory.GenerateInstruction(sInstr, instructionCounter);
                instructionList.add(newInstruction);
                if (!newInstruction.getLabel().equals(EMPTY_LABEL)) { // Case: add label iff it is not empty
                    Labels.put(newInstruction.getLabel(), newInstruction);
                }
                if (newInstruction.getDestinationLabel().equals(EXIT_LABEL)) {
                    containsExit = true;
                }
                instructionCounter++;
            }
            if (containsExit) {
                Instruction ExitInstruction = instructionFactory.GenerateExitInstruction(instructionList.size());
                Labels.put(EXIT_LABEL, ExitInstruction); // Special case: EXIT label
            }
            // Load program name
            programName = sProgram.getName();
            Set<Label> missingLabels = instructionFactory.getMissingLabels();
            if (!missingLabels.isEmpty()) {
                throw new IllegalArgumentException("The following labels are used but not defined: " + missingLabels);
            }});

        this.statistics = new Statistics();
        this.cycleCounter = 0;
        this.currentInstruction = instructionList.getFirst();
        this.runCounter = 1;
        this.currentProgramLevel = 0;
        this.maxProgramLevel = calculateMaxProgramLevel();
        this.usedXVariableNames = Variables.keySet().stream()
                .filter(name -> name.startsWith("x"))
                .collect(Collectors.toSet());
    }

    public void loadProgram(String filePath) throws FileNotFoundException, JAXBException{
        if (new File(filePath).exists()) {
            // Load JAXB
            JAXBContext jaxbContext = JAXBContext.newInstance(SProgram.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SProgram sProgram = (SProgram) jaxbUnmarshaller.unmarshal(new File(filePath));
            List<SInstruction> sInstructions = sProgram.getSInstructions().getSInstruction();


            // Load functions;

            Optional<SFunctions> sFunctionsOpt = Optional.ofNullable(sProgram.getSFunctions());
            sFunctionsOpt.ifPresent(sFunctions -> {
                functionsContainer.setup(sFunctions.getSFunction());
                functionsContainer.getFunctionNames().forEach(functionName -> {
                    try {
                        functionsContainer.tryGetFunction(functionName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            });




            // Load instructions
            InstructionFactory instructionFactory = new InstructionFactory(Variables, labelFactory, variableFactory, functionsContainer);
            int instructionCounter = 1;
            boolean containsExit = false;

            for (SInstruction sInstr : sInstructions) {
                Instruction newInstruction = instructionFactory.GenerateInstruction(sInstr, instructionCounter);
                instructionList.add(newInstruction);
                if (!newInstruction.getLabel().equals(EMPTY_LABEL)) { // Case: add label iff it is not empty
                    Labels.put(newInstruction.getLabel(), newInstruction);
                }
                if (newInstruction.getDestinationLabel().equals(EXIT_LABEL)) {
                    containsExit = true;
                }
                instructionCounter++;
            }
            if (containsExit) {
                Instruction ExitInstruction = instructionFactory.GenerateExitInstruction(instructionList.size());
                Labels.put(EXIT_LABEL, ExitInstruction); // Special case: EXIT label
            }
            // Load program name
            programName = sProgram.getName();
            Set<Label> missingLabels = instructionFactory.getMissingLabels();
            if (!missingLabels.isEmpty()) {
                throw new IllegalArgumentException("The following labels are used but not defined: " + missingLabels);
            }
        } else {
            throw new FileNotFoundException();
        }
    }

    private int calculateMaxProgramLevel() {
        // Calculate the maximum program level based on the instructions
        return instructionList.stream()
                .mapToInt(Instruction::getLevel)
                .max()
                .orElse(0);
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
                ExpandedSyntheticInstructionArguments singleExpandedInstruction = instruction.generateExpandedInstructions(labelFactory, variableFactory);
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

    public boolean isInDebugMode() {
        return isInDebugMode;
    }

    public Set<Label> getLabelNames() {
        return Labels.keySet();
    }

    public Map<Label, Instruction> getLabels() {
        return Labels;
    }

    public List<Instruction> getInstructionList() {
        return instructionList;
    }

    public String getProgramName() {
        return programName;
    }

    public Collection<Variable> getVariables() {
        return Variables.values();
    }

    public List<Instruction> getRuntimeExecutedInstructions() {
        return runtimeExecutedInstructions;
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

    public int getCurrentProgramLevel() {
        return currentProgramLevel;
    }

    public void setCycleCounter(int cycleCounter) {
        this.cycleCounter = cycleCounter;
    }

    public Set<Function> getFunctions() {
        return new HashSet<>(functionsContainer.getFunctions().values());
    }

    public void setInDebugMode(boolean inDebugMode) {
        isInDebugMode = inDebugMode;
    }

    public void setNextInstructionIdForDebug(int nextInstructionIdForDebug) {
        this.nextInstructionIdForDebug = nextInstructionIdForDebug;
    }

    public int getNextInstructionIdForDebug() {
        return nextInstructionIdForDebug;
    }

    public void AddYVariableIfNotExists() {
        if (!Variables.containsKey("y")) {
            Variables.put("y", new Variable("y", 0));
        }
    }
}