@echo off
echo ========================================
echo Analisis SonarQube - LabControl
echo ========================================

REM Verificar token
if not exist sonar-token.txt (
    echo ERROR: No se encontro sonar-token.txt
    echo Crea el archivo con tu token de SonarQube
    echo.
    echo Pasos:
    echo 1. Ve a http://localhost:9000
    echo 2. Login: admin/admin
    echo 3. My Account ^> Security ^> Generate Token
    echo 4. Guarda el token en sonar-token.txt
    pause
    exit /b 1
)

set /p SONAR_TOKEN=<sonar-token.txt

echo.
echo [1/4] Analizando Frontend...
cd frontend
call npm install --legacy-peer-deps
if errorlevel 1 (
    echo ERROR: npm install fallo
    cd ..
    pause
    exit /b 1
)
call npm run test:headless
if errorlevel 1 (
    echo ERROR: Tests del frontend fallaron
    cd ..
    pause
    exit /b 1
)
call npm run sonar -- -Dsonar.token=%SONAR_TOKEN%
if errorlevel 1 (
    echo ERROR: Analisis SonarQube del frontend fallo
    cd ..
    pause
    exit /b 1
)
cd ..

echo.
echo [2/4] Analizando ms-users...
cd ms-users
call mvnw.cmd clean test jacoco:report
if errorlevel 1 (
    echo ERROR: Tests de ms-users fallaron
    cd ..
    pause
    exit /b 1
)
call mvnw.cmd sonar:sonar -Dsonar.token=%SONAR_TOKEN% -Dsonar.host.url=http://localhost:9000
if errorlevel 1 (
    echo ERROR: Analisis SonarQube de ms-users fallo
    cd ..
    pause
    exit /b 1
)
cd ..

echo.
echo [3/4] Analizando ms-laboratorios...
cd ms-laboratorios
call mvnw.cmd clean test jacoco:report
if errorlevel 1 (
    echo ERROR: Tests de ms-laboratorios fallaron
    cd ..
    pause
    exit /b 1
)
call mvnw.cmd sonar:sonar -Dsonar.token=%SONAR_TOKEN% -Dsonar.host.url=http://localhost:9000
if errorlevel 1 (
    echo ERROR: Analisis SonarQube de ms-laboratorios fallo
    cd ..
    pause
    exit /b 1
)
cd ..

echo.
echo [4/4] Analizando ms-results...
cd ms-results
call mvnw.cmd clean test jacoco:report
if errorlevel 1 (
    echo ERROR: Tests de ms-results fallaron
    cd ..
    pause
    exit /b 1
)
call mvnw.cmd sonar:sonar -Dsonar.token=%SONAR_TOKEN% -Dsonar.host.url=http://localhost:9000
if errorlevel 1 (
    echo ERROR: Analisis SonarQube de ms-results fallo
    cd ..
    pause
    exit /b 1
)
cd ..

echo.
echo ========================================
echo Analisis completado exitosamente!
echo Ve a http://localhost:9000 para ver los resultados
echo ========================================
pause
