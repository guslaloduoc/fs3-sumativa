# Gestión de Laboratorios y Resultados de Análisis

**Actividad Sumativa - Experiencia 1**
**Asignatura:** Fullstack 3
**Institución:** DuocUC

---

## Descripción

Sistema backend para la gestión de laboratorios clínicos, control de usuarios y asignación de análisis. Implementado con arquitectura de microservicios usando Spring Boot y Oracle Cloud Database.

**Características principales:**
- ✅ Arquitectura de microservicios con DTOs
- ✅ Manejo centralizado de errores con @RestControllerAdvice
- ✅ Logging estructurado con SLF4J
- ✅ Validaciones de negocio robustas
- ✅ Seguridad mediante externalización de credenciales
- ✅ Tests unitarios con JUnit 5 y Mockito

---

## Arquitectura

El proyecto consta de 2 microservicios independientes:

### 1. **ms-users** (Puerto 8081)

Control de usuarios, roles e inicio de sesión.

**Funcionalidades:**
- Gestión completa de usuarios (CRUD)
- Sistema de roles y permisos
- Login simple (texto plano según requerimientos académicos)
- Validación de dominios de email autorizados (duocuc.cl, example.com)
- Protección de usuarios ADMIN (no eliminables/no deshabilitables)

### 2. **ms-laboratorios** (Puerto 8082)

Gestión de laboratorios y asignación de pacientes para análisis clínicos.

**Funcionalidades:**
- Gestión de laboratorios clínicos (CRUD)
- Asignación de pacientes a laboratorios
- Validación de nombres únicos
- Validación de formato de teléfono (7-15 dígitos)
- Protección contra eliminación de laboratorios con asignaciones activas

---

## Tecnologías

- **Java:** 21
- **Spring Boot:** 3.3.4
- **Base de Datos:** Oracle Cloud ATP (Autonomous Transaction Processing)
- **ORM:** Hibernate + JPA
- **Migraciones:** Flyway
- **Build:** Maven
- **Validación:** Bean Validation (javax.validation)
- **Logging:** SLF4J + Logback
- **Testing:** JUnit 5 + Mockito
- **Mapeo:** DTOs con mappers personalizados

---

## Instalación y Ejecución

### Requisitos previos:

- Java 21 o superior
- Maven 3.x
- Acceso a Oracle Cloud ATP (wallet configurado)

### 1. Configurar variables de entorno

Crear archivo `.env` en la raíz del proyecto (basado en `.env.example`):

```properties
DB_TNS_NAME=fs3_tp
TNS_ADMIN_PATH=./wallet
DB_USERNAME=ADMIN
DB_PASSWORD=tu_password_aqui
```

📖 **Ver guía completa:** [SECURITY_SETUP.md](SECURITY_SETUP.md)

### 2. Ejecutar ms-users:

```bash
cd ms-users
./mvnw.cmd clean spring-boot:run
```

**Puerto:** http://localhost:8081

### 3. Ejecutar ms-laboratorios:

```bash
cd ms-laboratorios
./mvnw.cmd clean spring-boot:run
```

**Puerto:** http://localhost:8082

---

## Estructura del Proyecto

```
sumativa/
├── ms-users/                          # Microservicio de usuarios
│   ├── src/main/java/
│   │   └── com/sumativa/ms_usuarios/
│   │       ├── controller/            # Endpoints REST
│   │       ├── service/               # Lógica de negocio
│   │       ├── repository/            # Acceso a datos (JpaRepository)
│   │       ├── entity/                # Entidades JPA (User, Role)
│   │       ├── dto/                   # DTOs (Request/Response)
│   │       ├── mapper/                # Conversión Entity <-> DTO
│   │       ├── exception/             # GlobalExceptionHandler
│   │       └── config/                # Configuración + DataInitializer
│   ├── src/main/resources/
│   │   ├── application.yml            # Configuración de Spring
│   │   └── db/migration/              # Scripts Flyway
│   ├── src/test/java/                 # Tests unitarios
│   │   ├── mapper/                    # Tests de mappers
│   │   └── service/                   # Tests de validaciones
│   └── pom.xml
│
├── ms-laboratorios/                   # Microservicio de laboratorios
│   ├── src/main/java/
│   │   └── com/sumativa/ms_laboratorios/
│   │       ├── controller/            # Endpoints REST
│   │       ├── service/               # Lógica de negocio
│   │       ├── repository/            # Acceso a datos
│   │       ├── entity/                # Entidades JPA (Laboratorio, Asignacion)
│   │       ├── dto/                   # DTOs (Request/Response)
│   │       ├── mapper/                # Conversión Entity <-> DTO
│   │       ├── exception/             # GlobalExceptionHandler
│   │       └── config/                # Configuración + DataInitializer
│   ├── src/main/resources/
│   │   ├── application.yml            # Configuración de Spring
│   │   └── db/migration/              # Scripts Flyway
│   ├── src/test/java/                 # Tests unitarios
│   │   ├── mapper/                    # Tests de mappers
│   │   └── service/                   # Tests de validaciones
│   └── pom.xml
│
├── Wallet_fs3/                        # Oracle Cloud Wallet (no versionado)
├── .env.example                       # Template de variables de entorno
├── .gitignore                         # Excluye wallet y credenciales
├── API_DOCUMENTATION.md               # Documentación completa de API
├── SECURITY_SETUP.md                  # Guía de configuración segura
├── DuocUC_Fullstack3_Microservices.postman_collection.json
└── README.md (este archivo)
```

---

## Credenciales de Prueba

### ms-users (Usuarios de prueba precargados)

```
Admin:    admin@example.com / admin123
Doctor:   juan.perez@duocuc.cl / doctor123
Lab Tech: maria.gonzalez@duocuc.cl / lab123
```

### Base de Datos Oracle

⚠️ **IMPORTANTE:** Las credenciales deben configurarse mediante variables de entorno:

```bash
# Configurar en .env (no versionado)
DB_TNS_NAME=fs3_tp
TNS_ADMIN_PATH=./wallet
DB_USERNAME=ADMIN
DB_PASSWORD=tu_password_aqui
```

Ver [SECURITY_SETUP.md](SECURITY_SETUP.md) para más detalles.

---

## Endpoints Principales

### ms-users (http://localhost:8081)

| Método | Endpoint                           | Descripción            |
| ------ | ---------------------------------- | ---------------------- |
| GET    | `/api/users`                       | Listar usuarios        |
| POST   | `/api/users`                       | Crear usuario          |
| GET    | `/api/users/{id}`                  | Obtener usuario por ID |
| PUT    | `/api/users/{id}`                  | Actualizar usuario     |
| DELETE | `/api/users/{id}`                  | Eliminar usuario       |
| POST   | `/api/users/login`                 | Iniciar sesión         |
| POST   | `/api/users/{id}/roles/{roleName}` | Asignar rol            |

### ms-laboratorios (http://localhost:8082)

| Método | Endpoint             | Descripción            |
| ------ | -------------------- | ---------------------- |
| GET    | `/laboratorios`      | Listar laboratorios    |
| POST   | `/laboratorios`      | Crear laboratorio      |
| GET    | `/laboratorios/{id}` | Obtener laboratorio    |
| PUT    | `/laboratorios/{id}` | Actualizar laboratorio |
| DELETE | `/laboratorios/{id}` | Eliminar laboratorio   |
| GET    | `/asignaciones`      | Listar asignaciones    |
| POST   | `/asignaciones`      | Crear asignación       |

📖 **Documentación completa de API con DTOs, ejemplos y manejo de errores:** [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

---

## Pruebas con Postman

1. Importar la colección: `DuocUC_Fullstack3_Microservices.postman_collection.json`

---

## Base de Datos

### ms-users

- **users** - Información de usuarios
- **roles** - Roles del sistema
- **user_roles** - Relación Many-to-Many

### ms-laboratorios

- **laboratorios** - Información de laboratorios clínicos
- **asignaciones** - Asignación de pacientes a laboratorios

**Datos iniciales:**

- 3 usuarios con diferentes roles
- 3 laboratorios (Santiago, Viña del Mar, La Serena)
- 5 asignaciones de pacientes

---

## Configuración

### Modificar puerto del servidor:

Editar `src/main/resources/application.yml`:

```yaml
server:
  port: 8081 # Cambiar según necesidad
```

### Configurar base de datos:

⚠️ **IMPORTANTE:** No codifiques credenciales en `application.yml`. Usa variables de entorno:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@${DB_TNS_NAME:fs3_tp}?TNS_ADMIN=${TNS_ADMIN_PATH:./wallet}
    username: ${DB_USERNAME:ADMIN}
    password: ${DB_PASSWORD}
```

Las credenciales reales deben configurarse en `.env` (ver sección Instalación).

---

## Roles del Sistema

- **ADMIN** - Administrador con acceso total
- **DOCTOR** - Médicos del sistema
- **LAB_TECH** - Técnicos de laboratorio
- **USER** - Usuario estándar

---

## Notas Importantes

- Las contraseñas se almacenan en **texto plano** según requerimientos académicos
- No se implementa sistema de pagos (fuera del alcance)
- Solo BackEnd, sin interfaz gráfica
- Ambos microservicios deben estar ejecutándose para pruebas completas

---

## Arquitectura Técnica

### DTOs (Data Transfer Objects)

Cada microservicio implementa una capa completa de DTOs para separar la representación de datos de la lógica de dominio:

**ms-users:**
- `UserResponseDto` - Respuestas de usuario (sin `passwordHash` por seguridad)
- `UserCreateDto` - Creación de usuarios con validaciones Bean Validation
- `UserUpdateDto` - Actualizaciones parciales (campos opcionales)
- `RoleResponseDto` - Información de roles
- `LoginRequest` / `LoginResponse` - Autenticación

**ms-laboratorios:**
- `LaboratorioResponseDto`, `LaboratorioCreateDto`, `LaboratorioUpdateDto`
- `AsignacionResponseDto`, `AsignacionCreateDto`, `AsignacionUpdateDto`

**Mappers:**
Clases utilitarias estáticas (`UserMapper`, `LaboratorioMapper`, `AsignacionMapper`) para conversión entre entidades y DTOs.

### Manejo Centralizado de Errores

Implementado mediante `@RestControllerAdvice` con `GlobalExceptionHandler`:

**Estructura consistente de errores:**
```json
{
  "timestamp": "2025-11-19T15:45:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "path": "/api/users",
  "fieldErrors": {
    "campo": "mensaje de validación"
  }
}
```

**Excepciones manejadas:**
- `MethodArgumentNotValidException` → 400 (errores de validación Bean Validation)
- `IllegalArgumentException` → 400 (errores de negocio)
- `NoSuchElementException` → 404 (recurso no encontrado)
- `Exception` → 500 (errores inesperados)

### Validaciones de Negocio

**ms-users:**
1. **Validación de dominio de email:** Solo se permiten emails con dominios `duocuc.cl` o `example.com`
2. **Protección de ADMIN:** No se pueden eliminar usuarios con rol ADMIN
3. **Usuario ADMIN principal:** El usuario `admin@example.com` no puede ser deshabilitado

**ms-laboratorios:**
1. **Nombres únicos:** No se permiten laboratorios con nombres duplicados (case-insensitive)
2. **Validación de teléfono:** Formato validado (7-15 dígitos, acepta espacios, guiones, paréntesis)
3. **Protección de datos:** No se pueden eliminar laboratorios con asignaciones activas

### Logging Estructurado

Implementado con **SLF4J + Logback** (`@Slf4j`):

- **INFO:** Operaciones exitosas (creación, actualización, eliminación, login)
- **WARN:** Validaciones fallidas, recursos no encontrados
- **ERROR:** Errores inesperados del servidor

**Seguridad:** Las contraseñas NUNCA se registran en logs.

### Testing

Tests unitarios con **JUnit 5 + Mockito**:

**ms-users:**
- `UserMapperTest` - Tests de conversión Entity ↔ DTO
- `UserServiceValidationTest` - Tests de reglas de negocio

**ms-laboratorios:**
- `LaboratorioMapperTest` - Tests de mappers
- `LaboratorioServiceValidationTest` - Tests de validaciones

**Ejecutar tests:**
```bash
# Desde el directorio de cada microservicio
./mvnw.cmd test

# Con reporte de cobertura (JaCoCo)
./mvnw.cmd clean test jacoco:report
# Ver reporte: target/site/jacoco/index.html
```

---

## Documentación Adicional

- 📄 [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - Documentación completa de API con ejemplos de request/response
- 🔒 [SECURITY_SETUP.md](SECURITY_SETUP.md) - Guía de configuración segura de wallet y credenciales
- 📋 [.env.example](.env.example) - Template de variables de entorno

---

## Licencia

Proyecto académico - DuocUC 2025

---

**Desarrollado con:** Spring Boot, Oracle Cloud, Java 21
