@echo off
:MENU
cls
echo ================================
echo   Explorador SQLite con Flask
echo ================================
echo.
echo 1. Iniciar servidor Flask
echo 2. Detener servidor Flask
echo 3. Salir
echo.
set /p option=Selecciona una opcion (1-3): 

if "%option%"=="1" goto START
if "%option%"=="2" goto STOP
if "%option%"=="3" goto END
goto MENU

:START
echo Iniciando servidor Flask...
start "" python app.py
timeout /t 3 >nul
start http://127.0.0.1:5000/
echo Servidor iniciado correctamente.
pause
goto MENU

:STOP
echo Deteniendo servidor Flask en puerto 5000...
set "PID="
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5000') do (
    set "PID=%%a"
)
if not "!PID!"=="" (
    echo Se encontró proceso con PID !PID!.
    taskkill /PID !PID! /F
    echo Flask detenido correctamente (PID !PID!).
) else (
    echo No se encontró proceso Flask en puerto 5000.
)
echo.
pause 
goto MENU

:END
echo Saliendo...
exit