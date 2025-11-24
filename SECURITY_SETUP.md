# Configuración de Seguridad - Microservicios DuocUC

Este documento explica cómo configurar el acceso a Oracle Cloud ATP de manera segura, sin versionar credenciales en git.

## 📋 Tabla de Contenidos

1. [Configuración del Oracle Wallet](#configuración-del-oracle-wallet)
2. [Variables de Entorno](#variables-de-entorno)
3. [Configuración por Entorno](#configuración-por-entorno)
4. [Troubleshooting](#troubleshooting)

---

## 🔐 Configuración del Oracle Wallet

### ¿Por qué NO versionar el wallet en git?

El wallet de Oracle Cloud contiene:
- Certificados de conexión segura
- Claves privadas y públicas
- Información de configuración de red

**NUNCA** debe versionarse en git por razones de seguridad.

### Paso 1: Obtener el Wallet

1. Accede a Oracle Cloud Console (cloud.oracle.com)
2. Navega a tu Autonomous Database (fs3)
3. Click en "DB Connection"
4. Descarga el wallet (Instance Wallet o Regional Wallet)
5. Descomprime el archivo ZIP

### Paso 2: Ubicar el Wallet

Tienes dos opciones:

#### Opción A: Wallet compartido (recomendado para desarrollo local)

```
sumativa/
├── Wallet_fs3/           # ⚠️ NO versionado en git
│   ├── cwallet.sso
│   ├── ewallet.p12
│   ├── tnsnames.ora
│   ├── sqlnet.ora
│   └── ...
├── ms-users/
└── ms-laboratorios/
```

En este caso, configure `TNS_ADMIN_PATH=../Wallet_fs3` en ambos microservicios.

#### Opción B: Wallet por microservicio

```
sumativa/
├── ms-users/
│   └── wallet/          # ⚠️ NO versionado en git
│       ├── cwallet.sso
│       └── ...
└── ms-laboratorios/
    └── wallet/          # ⚠️ NO versionado en git
        ├── cwallet.sso
        └── ...
```

En este caso, configure `TNS_ADMIN_PATH=./wallet` en cada microservicio.

### Paso 3: Verificar que el Wallet NO está versionado

```bash
# Ejecutar desde la raíz del proyecto
git status

# El wallet NO debe aparecer en la lista de cambios
# Si aparece, revisar el .gitignore
```

---

## 🌍 Variables de Entorno

### Variables Requeridas

| Variable | Descripción | Ejemplo | Default |
|----------|-------------|---------|---------|
| `DB_TNS_NAME` | Nombre del servicio TNS | `fs3_tp` | `fs3_tp` |
| `TNS_ADMIN_PATH` | Ruta al wallet | `./wallet` o `../Wallet_fs3` | `./wallet` |
| `DB_USERNAME` | Usuario de BD | `ADMIN` | `ADMIN` |
| `DB_PASSWORD` | Contraseña de BD | `Duocuc@.,2025` | ⚠️ **NO HAY DEFAULT** |

### Cómo Configurar Variables de Entorno

#### Windows (CMD)

```cmd
set DB_PASSWORD=Duocuc@.,2025
set TNS_ADMIN_PATH=../Wallet_fs3
set DB_USERNAME=ADMIN
set DB_TNS_NAME=fs3_tp
```

#### Windows (PowerShell)

```powershell
$env:DB_PASSWORD="Duocuc@.,2025"
$env:TNS_ADMIN_PATH="../Wallet_fs3"
$env:DB_USERNAME="ADMIN"
$env:DB_TNS_NAME="fs3_tp"
```

#### Linux/Mac

```bash
export DB_PASSWORD="Duocuc@.,2025"
export TNS_ADMIN_PATH="../Wallet_fs3"
export DB_USERNAME="ADMIN"
export DB_TNS_NAME="fs3_tp"
```

#### Usando archivo .env (Desarrollo Local)

1. Copiar el archivo de ejemplo:
   ```bash
   cp .env.example .env
   ```

2. Editar `.env` con tus valores:
   ```properties
   DB_PASSWORD=Duocuc@.,2025
   TNS_ADMIN_PATH=../Wallet_fs3
   DB_USERNAME=ADMIN
   DB_TNS_NAME=fs3_tp
   ```

3. **IMPORTANTE**: El archivo `.env` está en `.gitignore` y NO se versionará.

---

## ⚙️ Configuración por Entorno

### Desarrollo Local (DEV)

1. Descarga el wallet de Oracle Cloud
2. Ubícalo en `sumativa/Wallet_fs3/`
3. Configura variables de entorno (ver sección anterior)
4. Ejecuta el microservicio:
   ```bash
   cd sumativa/ms-users
   ./mvnw.cmd clean spring-boot:run
   ```

### IDE (IntelliJ IDEA / Eclipse)

#### IntelliJ IDEA

1. Run → Edit Configurations
2. Selecciona tu aplicación Spring Boot
3. En "Environment variables", agrega:
   ```
   DB_PASSWORD=Duocuc@.,2025;TNS_ADMIN_PATH=../Wallet_fs3
   ```

#### Eclipse

1. Run → Run Configurations
2. Selecciona tu aplicación
3. Tab "Environment"
4. Agrega las variables una por una

### Producción (PROD)

**NUNCA** uses las mismas credenciales de desarrollo en producción.

1. Obtén un wallet específico para producción
2. Configura variables de entorno en el servidor:
   - Usando systemd (Linux)
   - Usando variables de entorno del sistema operativo
   - Usando servicios de secretos (AWS Secrets Manager, Azure Key Vault, etc.)

3. Configura permisos estrictos en el wallet:
   ```bash
   chmod 600 /path/to/wallet/*
   chown app_user:app_group /path/to/wallet
   ```

---

## 🔧 Troubleshooting

### Error: "IO Error: Invalid connection string format"

**Causa**: No se encuentra el wallet o `TNS_ADMIN` está mal configurado.

**Solución**:
1. Verifica que el wallet existe en la ruta especificada
2. Verifica la variable `TNS_ADMIN_PATH`
3. Prueba con ruta absoluta: `TNS_ADMIN_PATH=C:/full/path/to/wallet`

### Error: "ORA-01017: invalid username/password"

**Causa**: Credenciales incorrectas.

**Solución**:
1. Verifica `DB_USERNAME` y `DB_PASSWORD`
2. Verifica que el usuario tiene permisos en Oracle Cloud
3. Considera resetear la contraseña en Oracle Cloud Console

### Error: "The Network Adapter could not establish the connection"

**Causa**: Problemas de red o configuración de TNS.

**Solución**:
1. Verifica que el archivo `tnsnames.ora` existe en el wallet
2. Verifica que `sqlnet.ora` existe y contiene:
   ```
   WALLET_LOCATION = (SOURCE = (METHOD = file) (METHOD_DATA = (DIRECTORY="?/network/admin")))
   SSL_SERVER_DN_MATCH=yes
   ```
3. Verifica conectividad de red con Oracle Cloud

### Error: "Variable de entorno DB_PASSWORD no definida"

**Causa**: No se configuró la variable de entorno.

**Solución**:
1. Configura la variable según tu sistema operativo (ver sección anterior)
2. Si usas IDE, configúrala en Run Configuration
3. Reinicia el terminal/IDE después de configurar

---

## 📚 Referencias

- [Oracle Cloud - Download Wallet](https://docs.oracle.com/en/cloud/paas/autonomous-database/adbsa/connect-download-wallet.html)
- [Spring Boot - Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Oracle JDBC Thin Driver](https://docs.oracle.com/en/database/oracle/oracle-database/21/jjdbc/introducing-jdbc.html)

---

## ⚠️ Recordatorios de Seguridad

1. ✅ Wallet en `.gitignore`
2. ✅ Credenciales en variables de entorno
3. ✅ NO compartir contraseñas en chat/email
4. ✅ Rotar credenciales periódicamente
5. ✅ Usar credenciales diferentes por entorno (DEV/PROD)
6. ✅ Permisos restrictivos en archivos del wallet (600 en Linux)
