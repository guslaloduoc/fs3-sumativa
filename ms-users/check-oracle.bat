@echo off
echo ============================================
echo Verificando configuracion Oracle Cloud ATP
echo ============================================
echo.

REM Verificar si existe la carpeta wallet
if exist "wallet\" (
    echo [OK] Carpeta wallet existe
) else (
    echo [ERROR] Carpeta wallet NO existe
    echo        Crea la carpeta wallet y copia los archivos del Wallet.zip
    goto error
)

REM Verificar archivos importantes del wallet
if exist "wallet\tnsnames.ora" (
    echo [OK] Archivo tnsnames.ora encontrado
) else (
    echo [ERROR] Archivo tnsnames.ora NO encontrado
    goto error
)

if exist "wallet\cwallet.sso" (
    echo [OK] Archivo cwallet.sso encontrado
) else (
    echo [ERROR] Archivo cwallet.sso NO encontrado
    goto error
)

echo.
echo ============================================
echo Configuracion del Wallet: OK
echo ============================================
echo.
echo Ahora verifica tu application.yml:
echo.
echo 1. Abre: src\main\resources\application.yml
echo.
echo 2. Busca tu servicio en wallet\tnsnames.ora
echo    Ejemplo: laboratdb_high
echo.
echo 3. Actualiza en application.yml:
echo    url: jdbc:oracle:thin:@TU_SERVICIO_high?TNS_ADMIN=./wallet
echo    username: ADMIN
echo    password: TU_PASSWORD_ADMIN
echo.
echo 4. Guarda el archivo
echo.
echo 5. Ejecuta: compile.bat
echo    Luego:   run.bat
echo.
echo ============================================

REM Mostrar servicios disponibles
echo Servicios disponibles en tnsnames.ora:
echo.
findstr /R "^[a-zA-Z].*=" wallet\tnsnames.ora
echo.
echo ============================================

pause
goto end

:error
echo.
echo ============================================
echo Sigue las instrucciones en:
echo INSTRUCCIONES_ORACLE.md
echo ============================================
pause

:end
