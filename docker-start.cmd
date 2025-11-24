@echo off
echo =======================================
echo LabControl - Iniciando Contenedores
echo =======================================
echo.

echo [1/3] Construyendo imagenes Docker...
docker-compose build

echo.
echo [2/3] Iniciando servicios...
docker-compose up -d

echo.
echo [3/3] Esperando que los servicios esten listos...
timeout /t 15 /nobreak > nul

echo.
echo =======================================
echo Servicios iniciados correctamente!
echo =======================================
echo.
echo Frontend:          http://localhost:4101
echo MS Users:          http://localhost:8081
echo MS Laboratorios:   http://localhost:8082
echo MS Results:        http://localhost:8083
echo.
echo Para ver logs: docker-compose logs -f
echo Para detener:  docker-compose down
echo.
pause
