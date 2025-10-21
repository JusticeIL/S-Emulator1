package user;

import XMLandJaxB.SFunction;
import XMLandJaxB.SProgram;
import controller.ProgramContainer;
import dto.Statistics;
import program.Program;
import program.function.Function;
import program.function.FunctionsContainer;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private int programsLoaded;
    private int functionsLoaded;
    private AtomicInteger credits;
    private int creditsUsed;
    private int programExecutionsCounter;
    private final String username;
    private final Statistics history;
    private final ProgramContainer programContainer;
    private final FunctionsContainer functionsContainer;
    private Program activeProgram;

    public User(String username) {
        this.username = username;
        this.programsLoaded = 0;
        this.functionsLoaded = 0;
        this.credits = new AtomicInteger(0);
        this.creditsUsed = 0;
        this.programExecutionsCounter = 0;
        this.history = new Statistics();
        this.programContainer = new ProgramContainer();
        this.functionsContainer = new FunctionsContainer();
    }

    public FunctionsContainer getFunctionsContainer() {
        return functionsContainer;
    }

    public ProgramContainer getProgramContainer() {
        return programContainer;
    }

    public Program getActiveProgram() {
        return activeProgram;
    }

    public void setActiveProgram(String program) {
        activeProgram = programContainer.getProgramExpansions(program).get(0);
        programContainer.setActiveProgramContainer(program);
        programContainer.setActiveProgramExpansionsByLevel(program);
    }

    public void addProgram(Program program) {
        programContainer.addProgram(program);
    }

    public String getUsername() {
        return username;
    }

    public int getProgramsLoaded() {
        return programsLoaded;
    }

    public void updateProgramsLoaded() {
        programsLoaded++;
    }

    public int getFunctionsLoaded() {
        return functionsLoaded;
    }

    public void setFunctionsLoaded(int functionsLoaded) {
        this.functionsLoaded = functionsLoaded;
    }

    public int getCredits() {
        return credits.get();
    }

    public int getCreditsUsed() {
        return creditsUsed;
    }

    public int getProgramExecutionsCounter() {
        return programExecutionsCounter;
    }

    public Statistics getHistory() {
        return history;
    }

    public void addCredits(int credits) {
        this.credits.getAndAdd(credits);
    }

    public void ExpandCurrentProgram(int level) {
        activeProgram = programContainer.ExpandProgram(activeProgram.getProgramName(), level) ;
    }

    public void switchToFunction(String functionName) {
        activeProgram = programContainer.getProgramExpansions(functionName).get(0);

        if (programContainer.getActiveProgramContainer().containsKey(functionName)) { // Case: the function name belongs to the main program
            programContainer.setActiveProgramExpansionsByLevel(functionName);
            activeProgram = programContainer.getActiveProgramExpansionsByLevel().get(0);
            return;
        }

        // Case: the function name belongs to a function
        activeProgram.getFunctions().stream()
                .filter(function -> function.getUserString().equals(functionName))
                .findFirst()
                .ifPresent(function -> {
                    programContainer.setActiveProgramExpansionsByLevel(function.getProgramName());
                    activeProgram = programContainer.getActiveProgramExpansionsByLevel().get(0);
                });
    }

    public boolean hasProgram(String programName) {
        return programContainer.getFullProgramContainer(programName) != null;
    }

    public int decreaseCredits(int cost) {
        int actualCost = cost <= credits.get() ? cost : credits.get();
        this.credits.getAndAdd(-cost);
        if(cost<0) {
            this.credits = new AtomicInteger(0);
        }

        this.creditsUsed += cost;
        return actualCost;
    }

    public void pullSfunctions(Set<SFunction> allSFunctions) {
        functionsContainer.setup(allSFunctions);
    }

    public void activateNewProgram(SProgram sProgram) {
        Program newProgramInstance = new Program(sProgram, this);
        addProgram(newProgramInstance);
        setActiveProgram(newProgramInstance.getProgramName());
    }

    public void activateNewProgram(SFunction sFunction){
        Program newProgramInstance = null;
        try {
            newProgramInstance = new Function(sFunction,this);
            addProgram(newProgramInstance);
            setActiveProgram(newProgramInstance.getProgramName());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("New Program Instance could not be created from Sfunction for user");
        }

    }
}
