# S-embly — Simple S-Language Emulator

## Authors

- Eitan Katz
- Gal Rubinstein

---

## Overview

This project is a simple emulator for the S-language, developed as part of a university Java programming assignment. It includes:

- A **GUI module** **JavaFX**-based GUI that handles user input and provides an interactive interface.
- An **engine module** that holds the emulator logic:
  - program representation (model).
  - instruction expansion.
  - debugging, including breakpoints and step over functionality.
  - instruction history representation.
  - program execution.
  - Highlighting labels and variables.
  - statistics.
  - credits handling.
  - architecture handling.
  - user data and user logic.

The architecture follows the **Model–View–Controller (MVC)** design pattern:
  - Model → User data, Program data, instructions, and statistics (engine).
  - View → **JavaFX**-based GUI.
  - Controller → Connects user actions in the GUI with the engine logic, composed of variety types of DTOs (**D**ata **T**ransfer **O**bjects).

The emulator now supports:
- Credits usage.
- Different architectures.
- Run other user's programs and runs.

---

## Setup Instructions

1. **Clone the Repository**  
   ```bash
   git clone https://github.com/JusticeIL/S-Emulator1.git
   ```
   
2. **Enter the correct folder**  
   If you are using Windows, navigate to the `Windows` folder:
   ```bash
   cd S-Emulator1/S-embly 3/Windows
   ```  
   If you are using Mac, navigate to the `Mac` folder:
   ```bash
   cd S-Emulator1/S-embly 3/Mac
   ```

3.**Copy .war file**  
   Copy the .WAR file from the `S-embly 3` folder (`S-emulator.war`) into the pre-defined tomcat webapps folder (`lib/tomcat/apache-tomcat-10.1.46/webapps`).

6.**Run the GUI S-Emulator program**  
   If you are using Windows, run the `run.bat` script from the `Windows` folder:
   ```bash
   run.bat
   ```  
   If you are using Mac, run the `run.sh` script from the `Mac` folder:
   ```bash
   ./run.sh
   ```

> 🔥 **Important:**  
> This project requires Java 21 or later (Oracle JDK).  
> The emulator only supports `.xml` program files that conform to a provided XML Schema (`.xsd`).
> Make sure the tomcat server is running on `localhost:8080` before starting the GUI.

---

## Program Features

- **Load XML Program:**  
  Load an S-language program definition from an XML file using JAXB.

- **Show Program:**  
  Display the program name, variables, labels, and all instructions with cycle counts, in a dedicated "execution" window.

- **Expand Program:**  
  Expand synthetic instructions into basic instructions up to a user-specified expansion level.
  Expansion is validated against the program’s maximum allowed level.

- **Run Program:**  
  Execute the program with user-provided input arguments.
  Provides execution of instructions, final variable states, and total cycles consumed.
  Allowing expansion up to the program’s maximum allowed level.
  
- **Run History / Statistics:**  
  Each user stores all executed runs, including run ID, expansion level, all arguments, final y value, and cycles inside the server.
  
- **Highlighting Instruction Components**  
  Allow the user to highlight instructions that use certain labels and variables in the program's instructions table.

- **Debugging**  
  Let the user set breakpoints on specific instructions, run the program step-by-step, and view the current state of variables and the next instruction to be executed,
  like it's a modern IDE!

- **Instruction History Chain**  
    Display the sequence of parent instructions in a dedicated table, sorted from newest to oldest.

- **CSS Skins**  
    Change the GUI appearance using different CSS stylesheets for better user experience and personal customization.

- **Use another user's history**  
  It is possible to rerun and execute another user's program or function from his dedicated statistics window.

---

## Notes

- The emulator parses XML files using **JAXB (Jakarta XML Binding)**.
- Labels are automatically sorted lexicographically, with `EXIT` always printed last.
- Expansion is supported up to each program’s maximum defined level.
- Both server and client sides uses HTTP protocol for network communication.

---

## License

This repository is intended for educational purposes only.
