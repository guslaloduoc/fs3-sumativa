@echo off
echo ====================================
echo ANALISIS SONARQUBE - FRONTEND
echo ====================================
echo.

echo [1/4] Verificando SonarQube...
echo Asegurate de que SonarQube este corriendo en http://localhost:9000
echo Presiona cualquier tecla para continuar o Ctrl+C para cancelar
pause > nul
echo.

echo [2/4] Ejecutando tests con cobertura...
echo Esto puede tardar 1-2 minutos...
call npm run test:headless
if %errorlevel% neq 0 (
    echo ERROR: Los tests fallaron!
    pause
    exit /b 1
)
echo.
echo ✓ Tests completados exitosamente
echo.

echo [3/4] Ejecutando analisis de SonarQube...
echo Enviando datos a SonarQube...
call sonar-scanner -Dsonar.login=admin -Dsonar.password=Admin@.,2025
if %errorlevel% neq 0 (
    echo ERROR: El analisis de SonarQube fallo!
    pause
    exit /b 1
)
echo.
echo ✓ Analisis completado exitosamente
echo.

echo [4/4] Resultados disponibles
echo ====================================
echo Abre tu navegador en:
echo http://localhost:9000/dashboard?id=labcontrol-frontend
echo ====================================
echo.
echo Presiona cualquier tecla para abrir el navegador...
pause > nul
start http://localhost:9000/dashboard?id=labcontrol-frontend

echo.
echo Analisis completado! Listo para grabar video.
pause
