@echo off
echo Deteniendo backend y frontend...

:: Detener Flask (puerto 5000)
taskkill /F /IM python.exe

:: Detener Live Server (Node.js)
taskkill /F /IM node.exe

echo Todo detenido.
pause