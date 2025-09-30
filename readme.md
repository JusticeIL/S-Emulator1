# S-embly — Simple S-Language Emulator

## Authors

- Eitan Katz
- Gal Rubinstein

---

## Overview

This project is a simple emulator for the S-language, developed as part of a university Java programming assignment. It includes:

- A **GUI module** that provides an interactive GUI, handles user input, and displays program output better than a console.
- An **engine module** that holds the emulator logic:
  - program representation (model).
  - instruction expansion.
  - debugging, including breakpoints and step over functionality.
  - instruction history representation.
  - program execution.
  - Highlighting labels and variables.
  - statistics.

The architecture follows the **Model–View–Controller (MVC)** design pattern:
  - Model → Program data, instructions, and statistics (engine).
  - View → **JavaFX**-based GUI.
  - Controller → Connects user actions in the GUI with the engine logic.

The emulator now supports:
- **Function invocation** allowing functions to be called with arguments.
- **Quotation** and **JEF** (**J**ump **E**qual **F**unction) instructions, for inserting function calls as arguments into other functions.
- Controller → Connects user actions in the GUI with the engine logic.

---

## Setup Instructions

1. **Clone the Repository**  
   ```bash
   git clone https://github.com/JusticeIL/S-Emulator1.git
   ```

2. **Copy .jar files**  
   Copy the two jars (`Engine.jar` and `GUI.jar`) into a new folder (named in English).

3. **Copy essential dependencies**  
   Copy the `lib` folder into the newly created folder.

4. **Open the new folder**  
   Open the newly created folder from the terminal:  
   ```bash
   cd <newly-created-folder-name>
   ```

5. **Run the console S-Emulator program**  
   Run the following command in the terminal:  
   ```bash
   java -jar GUI.jar
   ```

> 🔥 **Important:**  
> This project requires Java 21 or later (Oracle JDK).  
> The emulator only supports `.xml` program files that conform to a provided XML Schema (`.xsd`).

---

## Program Features

- **Load XML Program:**  
  Load an S-language program definition from an XML file using JAXB.
  The program replaces any previously loaded program only if the new one is valid.

- **Show Program:**  
  Display the program name, variables, labels, and all instructions with cycle counts.

- **Expand Program:**  
  Expand synthetic instructions into basic instructions up to a user-specified expansion level.
  Expansion is validated against the program’s maximum allowed level.

- **Run Program:**  
  Execute the program with user-provided input arguments.
  Provides execution of instructions, final variable states, and total cycles consumed.
  Allowing expansion up to the program’s maximum allowed level.
  
- **Run History / Statistics:**  
  Stores all executed runs, including run ID, expansion level, all arguments, final y value, and cycles.
  
- **Highlighting Instruction Components**  
  Allow the user to highlight instructions that use certain labels and variables in the program's instructions table.

- **Debugging**  
  Let the user set breakpoints on specific instructions, run the program step-by-step, and view the current state of variables and the next instruction to be executed,
  like it's a modern IDE!

- **Instruction History Chain**  
    Display the sequence of parent instructions in a dedicated table, sorted from newest to oldest.

- **CSS Skins**  
    Change the GUI appearance using different CSS stylesheets for better user experience and personal customization.

---

## Notes

- The emulator parses XML files using **JAXB (Jakarta XML Binding)**.
- Labels are automatically sorted lexicographically, with `EXIT` always printed last.
- Expansion is supported up to each program’s maximum defined level.

---

## License

This repository is intended for educational purposes only.
