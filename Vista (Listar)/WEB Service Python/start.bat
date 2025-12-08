@echo off
echo Iniciando backend Flask...
start cmd /k "python backend\app.py"

timeout /t 3

echo Iniciando frontend...
start cmd /k "npx live-server frontend --port=3000"