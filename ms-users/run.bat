@echo off
echo ============================================
echo Ejecutando ms-usuarios en puerto 8081
echo ============================================

REM Forzar el uso del JDK correcto
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo Java Home: %JAVA_HOME%
java -version
echo.

REM Ejecutar Spring Boot
mvnw.cmd spring-boot:run

pause
