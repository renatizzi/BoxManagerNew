@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

echo === BoxManager Famiglia - installazione sul telefono ===
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

echo [ERRORE] Java non trovato.
echo Usa CREA_APK_FAMIGLIA.bat e installa l APK a mano sul telefono.
pause
exit /b 1

:java_ok
echo Java: %JAVA_HOME%
echo.
echo Collega il TELEFONO con USB e debug USB attivo.
echo.
call gradlew.bat installFamigliaDebug
if errorlevel 1 (
    echo.
    echo Installazione fallita. Prova CREA_APK_FAMIGLIA.bat
    pause
    exit /b 1
)

echo.
echo FATTO. Apri "BoxManager Famiglia" sul telefono.
echo Versione attesa in topbar: 1.3-famigliaB5.7
pause
