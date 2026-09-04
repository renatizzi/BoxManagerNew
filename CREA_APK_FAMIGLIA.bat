@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

echo === BoxManager - crea APK sviluppo (senza Run di Android Studio) ===
echo.

if not exist "gradlew.bat" (
    echo Errore: esegui questo file dalla cartella BoxManagerNew.
    pause
    exit /b 1
)

set "JAVA_HOME="
for %%J in (
  "C:\Program Files\Android\Android Studio\jbr"
  "C:\Program Files\Android\Android Studio1\jbr"
  "%LOCALAPPDATA%\Programs\Android Studio\jbr"
  "%ProgramFiles%\Android\Android Studio\jbr"
) do (
  if exist %%~J\bin\java.exe (
    set "JAVA_HOME=%%~J"
    goto :java_ok
  )
)

echo [ERRORE] Java non trovato. Apri il progetto in Android Studio almeno una volta.
pause
exit /b 1

:java_ok
echo Java: %JAVA_HOME%
echo.
call gradlew.bat assembleFamigliaDebug
if errorlevel 1 (
    echo Build fallita.
    pause
    exit /b 1
)

set "APK=app\build\outputs\apk\famiglia\debug\app-famiglia-debug.apk"
echo.
echo APK creato:
echo   %CD%\%APK%
echo.
echo Sul TELEFONO:
echo   1. Collega USB (o invia file via Drive/WhatsApp)
echo   2. Apri il file APK e installa
echo   3. Consenti "origini sconosciute" se chiede
echo.
explorer /select,"%APK%"
pause
