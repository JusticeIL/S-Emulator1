@echo off
cd /d "%~dp0"
setlocal

echo [INFO] Running from: %CD%
echo.

where java
java -version
echo.

set NATIVE_DIR=%CD%\lib\javafx
echo [INFO] Native dir = "%NATIVE_DIR%"
set PATH=%NATIVE_DIR%;%PATH%

REM Module path where javafx jars are (these are jars, not DLLs)
set MODULE_PATH=%CD%\lib\javafx

java --module-path "%MODULE_PATH%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics ^
    -Djava.library.path="%NATIVE_DIR%" -Dprism.order=sw -jar Client.jar
if %ERRORLEVEL% == 0 goto :END