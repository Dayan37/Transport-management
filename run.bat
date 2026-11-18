@echo off
REM Compile and run without IntelliJ. Needs the MySQL Connector/J jar inside lib\
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
    if exist "%USERPROFILE%\.jdks\openjdk-27.0.1.0.3\bin\javac.exe" (
        set "PATH=%USERPROFILE%\.jdks\openjdk-27.0.1.0.3\bin;%PATH%"
    )
)
if not exist out mkdir out
javac -encoding UTF-8 -d out src\*.java && java -cp "out;lib\*" Main
pause
