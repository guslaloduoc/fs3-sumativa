@echo off
echo ============================================
echo Ejecutando ms-usuarios en MODO DESARROLLO
echo Base de datos: H2 (en memoria)
echo Puerto: 8081
echo Consola H2: http://localhost:8081/h2-console
echo ============================================

REM Forzar el uso del JDK correcto
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo Java Home: %JAVA_HOME%
java -version
echo.

REM Ejecutar con perfil dev (usa H2 en lugar de Oracle)
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

pause
