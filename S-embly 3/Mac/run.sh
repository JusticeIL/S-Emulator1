#!/bin/bash

APP_DIR=$(pwd)

echo "[INFO] Running from: $APP_DIR"
echo

# --- 1. Java Version Check (for logging) ---
which java
java -version
echo

# --- 2. Define Paths ---
# In macOS/Linux, the JavaFX JARS and native libraries are usually in the same directory.
# We include the specific JAR directories for external libs as well (OkHttp, Kotlin, etc.)
# Note: For -jar to work, external dependencies MUST be listed in the Manifest file.
# The JavaFX JARs must be on the module path (--module-path).
MODULE_PATH="$APP_DIR/lib/javafx"
# NOTE: The -Djava.library.path is often unnecessary for JavaFX on modern macOS/Unix builds 
# when using --module-path, but we include it for compatibility with the Windows logic.
NATIVE_DIR="$APP_DIR/lib/javafx"

echo "[DEBUG] Module path = $MODULE_PATH"
echo "[DEBUG] Native dir = $NATIVE_DIR"

# --- 3. Run the Application ---
# The -jar option implies the main class and classpath come from the MANIFEST.MF.
# However, for modular JavaFX, we must still supply the module path and add-modules arguments.
java \
  --module-path "$MODULE_PATH" \
  --add-modules=javafx.controls,javafx.fxml,javafx.graphics \
  -Djava.library.path="$NATIVE_DIR" \
  -Dprism.order=sw \
  -jar Client.jar

# --- 4. Check Exit Status ---
if [ $? -eq 0 ]; then
    echo "[INFO] Application finished successfully."
else
    echo "[ERROR] Application exited with code $?."
    echo "Check the console output above for potential errors (e.g., FXML loading, network)."
fi