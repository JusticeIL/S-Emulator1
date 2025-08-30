package console.ui;

import controller.Controller;
import controller.SingleProgramController;
import jakarta.xml.bind.JAXBException;
import program.Program;
import program.ProgramData;
import program.Run;
import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ConsoleUI {

    private Controller engine = new SingleProgramController();
    private final Scanner scanner = new Scanner(System.in);
    private final String serializationFileType = ".ser";
    private final long WAIT = 1;

    public static void main(String[] args) {
        new ConsoleUI().run();
    }

    public void run() {
        System.out.println("Welcome to S-Emulator!");

        boolean exit = false;
        while (!exit) {
            showMenu();
            System.out.print("choose an option (1-8): ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> handleLoadXml();
                    case 2 -> handleShowProgram();
                    case 3 -> handleExpand();
                    case 4 -> handleRun();
                    case 5 -> handleShowHistory();
                    case 6 -> handleSaveState();
                    case 7 -> handleLoadState();
                    case 8 -> {
                        System.out.print("Exiting. Goodbye!");
                        exit = true;
                    }
                    default ->
                            System.out.println("Option does not exist in the menu. Please choose a number that exists in the menu.");
                }
            } catch (NumberFormatException e) { // Case: user inputs a non-integer
                System.out.println("Invalid input. Please enter a number between 1 and 8.");
            }
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(WAIT));
            } catch (InterruptedException e) { // Case: someone interrupted the sleep of the main thread
                System.out.println("Could not wait due to another thread interruption.");
            }
        }
        scanner.close();
    }

    private void showMenu() {
        System.out.println("==== Main Menu ====");
        System.out.println("1) Load XML program");
        System.out.println("2) Show current program");
        System.out.println("3) Expand program");
        System.out.println("4) Run program");
        System.out.println("5) Show run history / statistics");
        System.out.println("6) Save state");
        System.out.println("7) Load state");
        System.out.println("8) Exit");
    }


    private void handleLoadXml() {
        System.out.print("Enter full XML path: ");
        String path = scanner.nextLine().trim();

        if (!path.endsWith(".xml")) { // Case: not a xml file
            System.out.println("file must be an XML file (AKA ends with .xml)");
            return;
        }

        try {
            engine.loadProgram(path);
            System.out.println("Program loaded successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("Error: file " + path + " not found!");
        } catch (InvalidPathException e) {
            System.out.println("Error: invalid file path entered: " + path + "." + "Please check the path format.");
        } catch (JAXBException e) {
            System.out.println("Error: Malformed XML file detected: " + Paths.get(path).getFileName());
        } catch (Exception e) { // Case: general exception
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleShowProgram() {
        if (!engine.isProgramLoaded()) { // Case: no program was loaded
            System.out.println("No program loaded.");
            System.out.println("Load program first!");
            return;
        }
        engine.Expand(0);
        engine.getProgramData().ifPresentOrElse(programData -> {
            System.out.println("Current Program Name: " + programData.getProgramName());
            System.out.println("Variables: " + programData.getProgramXArguments());
            System.out.println("Labels: " + programData.getProgramLabels().stream()
                    .sorted((a, b) -> {
                        if (a.equals(Program.EXIT_LABEL.getLabelName())) return 1;
                        if (b.equals(Program.EXIT_LABEL.getLabelName())) return -1;
                        return a.compareTo(b);
                    }).toList());
            System.out.println("Instructions: ");
            programData.getProgramInstructions().forEach(System.out::println);
        }, () -> {
            System.out.println("No program loaded.");
            System.out.println("Load program first!");
        });
    }

    private void handleExpand() {
        if (!engine.isProgramLoaded()) { // Case: no program was loaded
            System.out.println("No program loaded.");
            System.out.println("Load program first!");
            return;
        }
        engine.Expand(0); // Reset current active program

        if (engine.getProgramData().get().getMaxExpandLevel() == 0) { // Case: program cannot be expanded
            System.out.println("This program " + "(" + engine.getProgramData().get().getProgramName() + ")" + " cannot be expanded.");
            return;
        }

        Optional<ProgramData> programDataOpt = engine.getProgramData();
        final int maxLevel = programDataOpt.get().getMaxExpandLevel();

        int level = -1;
        while (level <= 0) {
            System.out.print("Enter expansion level between 1 and " + maxLevel + " (positive number): ");
            String input = scanner.nextLine().trim();
            try {
                level = Integer.parseInt(input);
                if (level <= 0) {
                    System.out.println("Expansion level must be a positive number.");
                    continue;
                }
                engine.Expand(level);
                 engine.getProgramData().get().getProgramInstructions().forEach(System.out::println);

            } catch (NumberFormatException e) { // Case: user input was not a number
                System.out.println("Invalid input. Please enter a positive number.");
                level = -1;
            } catch (IllegalArgumentException e) { // Case: expansion level is too high
                System.out.println(e.getMessage());
                level = -1;
            }

            try { // Just to make sure the user has time to read the output before the next line shows up
                Thread.sleep(TimeUnit.SECONDS.toMillis(WAIT));
            } catch (InterruptedException e) { // Case: someone interrupted the sleep of the main thread
                System.out.println("Could not wait due to another thread interruption.");
            }
        }
    }

    private void handleRun() {
        if (!engine.isProgramLoaded()) { // Case: no program was loaded
            System.out.println("No program loaded.");
            System.out.println("Load program first!");
            return;
        }
        engine.Expand(0);
        Optional<ProgramData> programDataOpt = engine.getProgramData();
        final int maxLevel = programDataOpt.get().getMaxExpandLevel();

        int level = -1;
        while (level < 0 || level > maxLevel) {
            System.out.print("Enter expansion level between 0 and " + maxLevel + " (0 for no expansion): ");
            String input = scanner.nextLine().trim();
            try {
                level = Integer.parseInt(input);
                if (level < 0) { // Case: number is not valid
                    System.out.println("Expansion level must be a non-negative integer.");
                    level = -1;
                }
                else if (level > maxLevel) { // Case: the user number input is higher than the max level of the program
                    System.out.println("Expansion level must be between 0 and " + maxLevel + ".");
                    level = -1;
                }
            }
            catch (NumberFormatException e) {
                System.out.println("The provided expansion level is illegal.");
                level = -1;
            }
            catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                level = -1;
            }
        }
        int[] args = null;
        while (args == null) {
            System.out.println("Program x arguments: " + programDataOpt.get().getProgramXArguments());
            System.out.print("Enter x arguments separated by ',': ");
            String argsInput = scanner.nextLine().trim();
            try {
                String[] parts = argsInput.split(",");
                args = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    args[i] = Integer.parseInt(parts[i].trim());
                    if (args[i] < 0) {
                        throw new NumberFormatException("All arguments must be non-negative integers.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please re-enter the arguments for the program.");
                args = null;
            }
        }

        // Safe to run the program
        engine.Expand(level);
        engine.runProgram(args);
        programDataOpt = engine.getProgramData(); // Refresh program data after run
        System.out.println("Executed instructions:");
        programDataOpt.get().getRuntimeExecutedInstructions().forEach(System.out::println);
        programDataOpt.get().getProgramVariablesCurrentState().forEach(System.out::println);
        System.out.println("Program cycles: " + engine.getProgramData().get().getCurrentCycles());
    }

    private void handleShowHistory() {
        Optional<ProgramData> programDataOpt = engine.getProgramData();
        if (!engine.isProgramLoaded()) { // Case: no program loaded
            System.out.println("No program loaded.");
            System.out.println("Load program first!");
            return;
        }

        List<Run> history = programDataOpt.get().getStatistics().getHistory();

        if (history.isEmpty()) { // Case: no runs have been executed yet
            System.out.println("No runs have been executed yet.");
            return;
        }

        history.stream()
                .map(run -> String.format(
                        """
                                Run ID: %d |
                                Expansion Level: %d |
                                Input Args: %s |
                                y value: %d |
                                Cycles: %d""",
                        run.getRunID(),
                        run.getExpansionLevel(),
                        run.getInputArgs().entrySet().stream()
                                .map(entry -> entry.getKey() + " = " + entry.getValue())
                                .toList(),
                        run.getyValue(),
                        run.getRunCycles()
                )).forEach(System.out::println);
    }

    private void handleSaveState() {
        System.out.print("Enter full path (including file name and excluding file type) to save the state: ");
        String filePath = scanner.nextLine().trim();
        try {
            filePath += serializationFileType;
            try (ObjectOutputStream out =
                         new ObjectOutputStream(
                                 new FileOutputStream(filePath))) {
                Optional<ProgramData> programDataOpt = engine.getProgramData();
                if(programDataOpt.isPresent()){
                    engine.Expand(0); // Reset program to level 0 before saving
                    engine.Expand(programDataOpt.get().getMaxExpandLevel());//Save as maximum expansion level
                }
                out.writeObject(engine);
                out.flush();
                System.out.println("State saved successfully!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error with creating the serialization file");
        } catch (IOException e) {
            System.out.println("Potential I/O error happend: " + e.getMessage());
        }
    }

    private void handleLoadState() {
        System.out.print("Enter full path to the load the state from the file (excluding file type): ");
        String filePath = scanner.nextLine().trim();
        filePath += serializationFileType;
        try (ObjectInputStream in =
                new ObjectInputStream(
                        new FileInputStream(filePath))) {
            engine = (Controller) in.readObject();
            System.out.println("State loaded successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("Could not find the serialization file.");
            System.out.println("Please save the state first.");
        } catch (IOException e) {
            System.out.println("Potential I/O error happend: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}