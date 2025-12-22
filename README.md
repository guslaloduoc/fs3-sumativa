# Sistema de Gestion de Laboratorios - LabControl

Sistema Full Stack para la gestion de laboratorios clinicos y resultados de analisis.

## Arquitectura

### Backend - Microservicios Spring Boot
- ms-users (Puerto 8081): Gestion de usuarios y autenticacion
- ms-laboratorios (Puerto 8082): Gestion de laboratorios y asignaciones
- ms-results (Puerto 8083): Gestion de resultados de analisis

### Frontend - Angular 21
- Puerto 4200: Interfaz de usuario responsive
- Bootstrap 5 + Bootstrap Icons
- Reactive Forms con validaciones

### Base de Datos
- Oracle Cloud ATP (Autonomous Transaction Processing)
- Wallet: sumativa/Wallet_fs3/
- Flyway para migraciones

## Instrucciones de Ejecucion

### Opcion 1: Con Docker (Recomendado)

Prerrequisitos:
- Docker Desktop instalado
- Docker Compose instalado

Paso 1 - Configurar variables de entorno:
```
cd sumativa
copy .env.example .env
# Editar .env con tus credenciales de Oracle Cloud
```

Paso 2 - Iniciar todos los servicios:
```
# Windows
docker-start.cmd

# O manualmente
docker-compose up -d --build
```

Paso 3 - Acceder a la aplicacion:
- Frontend: http://localhost:4200
- MS Users: http://localhost:8081
- MS Laboratorios: http://localhost:8082
- MS Results: http://localhost:8083

Comandos utiles:
```
# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down

# Reconstruir imagenes
docker-compose build --no-cache
```

### Opcion 2: Ejecucion Local (Sin Docker)

Prerrequisitos:
- Java 21
- Node.js 20+
- Maven 3.x
- Oracle Cloud ATP configurado

Terminal 1 - MS Users:
```
cd sumativa/ms-users
./mvnw.cmd clean spring-boot:run
```

Terminal 2 - MS Laboratorios:
```
cd sumativa/ms-laboratorios
./mvnw.cmd clean spring-boot:run
```

Terminal 3 - MS Results:
```
cd sumativa/ms-results
./mvnw.cmd clean spring-boot:run
```

Terminal 4 - Frontend:
```
cd sumativa/frontend
npm install
npm start
```

Acceder a: http://localhost:4200

## Usuarios de Prueba

Usuario Administrador:
- Email: admin@labcontrol.com
- Password: Admin123!
- Acceso: Completo (Usuarios, Laboratorios, Resultados)

Usuario Paciente:
- Email: patient@labcontrol.com
- Password: Patient123!
- Acceso: Dashboard, Laboratorios (lectura), Resultados (lectura)

## Caracteristicas del Frontend

### Autenticacion
- Login con validacion
- Registro con validaciones de password fuerte
- Recuperacion de password
- Proteccion de rutas por roles

### Gestion de Usuarios (Solo ADMIN)
- CRUD completo de usuarios
- Gestion de roles
- Habilitar/Deshabilitar usuarios

### Gestion de Laboratorios
- Vista en cards responsive
- CRUD completo (Solo ADMIN)

### Gestion de Resultados
- CRUD completo
- Permisos: ADMIN y LAB_TECH

## APIs Endpoints

MS-Users (8081):
- POST /api/users/login
- GET /api/users
- POST /api/users
- PUT /api/users/{id}
- DELETE /api/users/{id}

MS-Laboratorios (8082):
- GET /laboratorios
- POST /laboratorios
- PUT /laboratorios/{id}
- DELETE /laboratorios/{id}

MS-Results (8083):
- GET /api/resultados
- POST /api/resultados
- PUT /api/resultados/{id}
- DELETE /api/resultados/{id}

## Arquetipo Maven

El proyecto incluye un **arquetipo Maven** que permite generar nuevos microservicios Spring Boot con la misma estructura y configuración.

### ✅ Evidencia de Uso del Arquetipo

**Ver documento oficial**: `EVIDENCIA_USO_ARQUETIPO.md`

El arquetipo ha sido:
- ✅ Creado a partir de ms-users
- ✅ Instalado en repositorio local Maven
- ✅ Documentado completamente
- ✅ Probado generando `ms-demo-archetype`

### Generar Nuevo Microservicio

```cmd
cd sumativa
mvn archetype:generate ^
  -DarchetypeGroupId=com.sumativa ^
  -DarchetypeArtifactId=ms-usuarios-archetype ^
  -DarchetypeVersion=0.0.1-SNAPSHOT ^
  -DgroupId=com.sumativa ^
  -DartifactId=ms-nuevo-servicio ^
  -Dversion=0.0.1-SNAPSHOT ^
  -Dpackage=com.sumativa.ms_nuevo_servicio
```

**Documentación completa**:
- `springboot-microservice-archetype/README.md` - Manual completo
- `springboot-microservice-archetype/GUIA_RAPIDA.md` - Guía rápida
- `EVIDENCIA_USO_ARQUETIPO.md` - Evidencia oficial

## Tecnologias

Backend: Java 21, Spring Boot 3.3.4, Oracle JDBC, Flyway
Frontend: Angular 21, TypeScript, Bootstrap 5
Base de Datos: Oracle Cloud ATP
Contenedores: Docker, Docker Compose, Nginx
Arquetipo: Maven Archetype para generación de microservicios

## Arquitectura Docker

### Imagenes
- labcontrol-frontend: Nginx + Angular (Puerto 80 -> 4200)
- labcontrol-ms-users: Spring Boot (Puerto 8081)
- labcontrol-ms-laboratorios: Spring Boot (Puerto 8082)
- labcontrol-ms-results: Spring Boot (Puerto 8083)

### Red
- labcontrol-network: Red bridge para comunicacion entre contenedores

### Volumenes
- Wallet_fs3: Montado como read-only en todos los microservicios

### Health Checks
Todos los servicios tienen health checks configurados:
- Frontend: cada 30s
- Backend: cada 30s con 60s de inicio

## Estructura de Archivos Docker

```
sumativa/
├── docker-compose.yml          # Orquestacion de servicios
├── .env                        # Variables de entorno (NO subir a git)
├── .env.example               # Ejemplo de variables
├── docker-start.cmd           # Script de inicio Windows
├── docker-stop.cmd            # Script de parada Windows
├── docker-logs.cmd            # Script para ver logs
│
├── frontend/
│   ├── Dockerfile             # Build multi-stage Angular + Nginx
│   ├── nginx.conf             # Configuracion Nginx
│   └── .dockerignore          # Archivos a excluir del build
│
├── ms-users/
│   ├── Dockerfile             # Build multi-stage Maven + JRE
│   └── .dockerignore
│
├── ms-laboratorios/
│   ├── Dockerfile
│   └── .dockerignore
│
└── ms-results/
    ├── Dockerfile
    └── .dockerignore
```

## Analisis de Calidad con SonarQube

### Requisitos Previos

1. **SonarQube corriendo** (incluido en docker-compose):
   - URL: http://localhost:9000
   - Usuario inicial: `admin` / `admin`

2. **Token de SonarQube** (crear una sola vez):
   - Ir a http://localhost:9000
   - My Account > Security > Generate Token
   - Guardar el token en `sonar-token.txt`

### Ejecutar Analisis Completo

```powershell
.\run-sonar-analysis.cmd
```

### Que hace el script `run-sonar-analysis.cmd`

El script ejecuta tests y analisis de SonarQube para cada componente:

| Paso | Componente      | Comandos Ejecutados                                           |
|------|-----------------|---------------------------------------------------------------|
| 1/4  | Frontend        | `npm install` + `npm run test:headless` + `npm run sonar`     |
| 2/4  | ms-users        | `mvnw clean test jacoco:report` + `mvnw sonar:sonar`          |
| 3/4  | ms-laboratorios | `mvnw clean test jacoco:report` + `mvnw sonar:sonar`          |
| 4/4  | ms-results      | `mvnw clean test jacoco:report` + `mvnw sonar:sonar`          |

### Ejecutar Tests Manualmente

**Frontend (Angular con Karma/Jasmine):**
```bash
cd frontend
npm run test              # Tests con browser
npm run test:headless     # Tests sin browser (CI/CD)
```

**Microservicios (JUnit 5 + JaCoCo):**
```bash
cd ms-users               # o ms-laboratorios, ms-results
.\mvnw.cmd test                           # Solo tests
.\mvnw.cmd clean test jacoco:report       # Tests + reporte cobertura
```

### Ver Resultados

| Recurso                | URL / Ubicacion                                    |
|------------------------|----------------------------------------------------|
| SonarQube Dashboard    | http://localhost:9000                              |
| Reporte JaCoCo         | `{microservicio}/target/site/jacoco/index.html`    |
| Reporte Karma          | `frontend/coverage/index.html`                     |

### Metricas Evaluadas por SonarQube

- **Bugs**: Errores potenciales en el codigo
- **Vulnerabilities**: Problemas de seguridad
- **Code Smells**: Codigo que puede mejorarse
- **Coverage**: Porcentaje de codigo cubierto por tests
- **Duplications**: Codigo duplicado

## Proyecto

Evaluacion Sumativa - Desarrollo Full Stack III (DSY2205)
DuocUC - 2025
