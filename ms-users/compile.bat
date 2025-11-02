@echo off
echo ============================================
echo Compilando ms-usuarios con Java 21
echo ============================================

REM Forzar el uso del JDK correcto
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo Java Home: %JAVA_HOME%
java -version
echo.

REM Limpiar y compilar
mvnw.cmd clean install -DskipTests

echo.
echo ============================================
echo Compilacion completada!
echo ============================================
pause
