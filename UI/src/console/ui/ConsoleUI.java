package console.ui;

import controller.Controller;
import controller.SingleProgramController;
import jakarta.xml.bind.JAXBException;
import program.Program;
import program.ProgramData;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {

    private final Scanner in = new Scanner(System.in);
    private final Controller engine = new SingleProgramController();

    public static void main(String[] args) {
        new ConsoleUI().run();
    }

    public void run() {
        System.out.println("Welcome to S-Emulator!");

        boolean exit = false;
        while (!exit) {
            showMenu();
            System.out.print("choose an option (1-6): ");
            try {
                int choice = in.nextInt(); // TODO: make it readline and parse it later
                in.nextLine();
                switch (choice) {
                    case 1 -> handleLoadXml();
                    case 2 -> handleShowProgram();
                    case 3 -> handleExpand();
                    case 4 -> handleRun();
//                    case 5 -> handleShowHistory();
                    case 6 -> {
                        System.out.println("\nExiting. Goodbye!");
                        exit = true;
                    }
                }
            } catch (InputMismatchException e) { // Case: user inputs a non-integer
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
            }
        }
        in.close();
    }

    private void handleExpand() {
        if (!engine.isProgramLoaded()) {
            System.out.println("No program loaded.");
            System.out.println("Load program first!");
            return;
        }
        System.out.print("Enter expansion level (positive integer): ");
        String input = in.nextLine().trim();
        try {
            int level = Integer.parseInt(input);
            if (level <= 0) {
                System.out.println("Expansion level must be a positive integer.");
                return;
            }
            engine.Expand(level);
            engine.getProgramData().get().getExpandedProgramInstructions().forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a positive integer.");
        }
    }

    private void showMenu() {
        System.out.println("==== Main Menu ====");
        System.out.println("1) Load XML program");
        System.out.println("2) Show current program");
        System.out.println("3) Expand program");
        System.out.println("4) Run program");
        System.out.println("5) Show run history / statistics");
        System.out.println("6) Exit");
    }


    private void handleLoadXml() {
        System.out.println("Enter full XML path: ");
        String path = in.nextLine().trim();

        if (!path.endsWith(".xml")) { // Case: not a xml file
            System.out.println("file must be an XML file (AKA ends with .xml)");
            return;
        }

        try {
            engine.loadProgram(path);
            System.out.println("Program loaded successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("Error: file " + Paths.get(path).getFileName() + " not found!");
        } catch (JAXBException e) {
            System.out.println("Error: Malformed XML file detected: " + Paths.get(path).getFileName());
        } catch (Exception e) {
            System.out.println("Unexpected error while loading: " + e.getMessage());
        }
    }

    private void handleShowProgram() {
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

    private void handleRun() {
        Optional<ProgramData> programDataOpt = engine.getProgramData();
        if (!engine.isProgramLoaded()) {
            System.out.println("No program loaded.");
            System.out.println("Load program first!");
            return;
        }
        System.out.print("Enter expansion level (0 for no expansion): ");
        String input = in.nextLine().trim();
        try {
            int level = Integer.parseInt(input);
            if (level < 0) {
                System.out.println("Expansion level must be a non-negative integer.");
                return;
            }
            System.out.println("Program x arguments: " + programDataOpt.get().getProgramXArguments());
            System.out.print("Enter x arguments separated by ',': ");
            String argsInput = in.nextLine().trim();
            String[] parts = argsInput.split(",");
            int[] args = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                args[i] = Integer.parseInt(parts[i].trim());
                if (args[i] < 0) {
                    System.out.println("All arguments must be non-negative integers.");
                    return;
                }
            }
            engine.Expand(level);
            engine.runProgram(args);
            engine.getProgramData().get().getProgramVariablesCurrentState().forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter non-negative integers separated by ','.");
        }
    }

}






