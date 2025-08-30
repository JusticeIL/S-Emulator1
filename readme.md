# S-embly — Simple S-Language Emulator

## Authors

- Eitan Katz
- Gal Rubinstein

---

## Overview

This project implements a small emulator for the S-language designed for a university java programming assignment. It includes:

- A **console UI module** that drives the program (menu, user input validation and printing).
- An **engine module** that holds the emulator logic:
  - program model.
  - instruction expansion.
  - statistics.

---

## Setup Instructions

1. **Clone the Repository**  
   ```bash
   git clone https://github.com/JusticeIL/S-Emulator1.git
   ```

2. **Copy .jar files**  
   Copy the two jars (`Engine.jar` and `UI.jar`) into a new folder (named in English).

3. **Copy essential dependencies**  
   Copy the `mod` folder into the newly created folder.

4. **Open the new folder**  
   Open the newly created folder from the terminal:  
   ```bash
   cd <newly-created-folder-name>
   ```

5. **Run the console S-Emulator program**  
   Run the following command in the terminal:  
   ```bash
   java -jar UI.jar
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
  Prints executed instructions, final variable states, and total cycles consumed.
  Allowing expansion up to the program’s maximum allowed level.
  
- **Run History / Statistics:**  
  Stores all executed runs, including run ID, expansion level, input arguments, final y value, and cycles.
  
- **Save & Load State:**  
  Serialize and restore the current program, run history, and relevant static state for later use.

---

## Notes

- The emulator parses XML files using **JAXB (Jakarta XML Binding)**.
- State persistence (save/load) is implemented using **Java Serialization**.
- Labels are automatically sorted lexicographically, with `EXIT` always printed last.
- Expansion is supported up to each program’s maximum defined level.

---

## License

This repository is intended for educational purposes only.
