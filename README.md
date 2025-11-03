# Gestión de Laboratorios y Resultados de Análisis

**Actividad Sumativa - Experiencia 1**
**Asignatura:** Fullstack 3
**Institución:** DuocUC

---

## Descripción

Sistema backend para la gestión de laboratorios clínicos, control de usuarios y asignación de análisis. Implementado con arquitectura de microservicios usando Spring Boot y Oracle Cloud Database.

---

## Arquitectura

El proyecto consta de 2 microservicios independientes:

### 1. **ms-users** (Puerto 8081)

Control de usuarios, roles e inicio de sesión.

### 2. **ms-laboratorios** (Puerto 8082)

Gestión de laboratorios y asignación de pacientes para análisis clínicos.

---

## Tecnologías

- **Java:** 21
- **Spring Boot:** 3.3.4
- **Base de Datos:** Oracle Cloud ATP (Autonomous Transaction Processing)
- **ORM:** Hibernate + JPA
- **Migraciones:** Flyway
- **Build:** Maven
- **Validación:** Bean Validation

---

## Instalación y Ejecución

### Requisitos previos:

- Java 21 o superior
- Maven 3.x
- Acceso a Oracle Cloud ATP (wallet configurado)

### Ejecutar ms-users:

```bash
cd ms-users
./mvnw.cmd spring-boot:run
```

**Puerto:** http://localhost:8081

### Ejecutar ms-laboratorios:

```bash
cd ms-laboratorios
./mvnw.cmd spring-boot:run
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
│   │       ├── repository/            # Acceso a datos
│   │       ├── entity/                # Entidades JPA
│   │       ├── dto/                   # Data Transfer Objects
│   │       └── config/                # Configuración
│   ├── src/main/resources/
│   │   ├── application.yml            # Configuración de Spring
│   │   └── db/migration/              # Scripts Flyway
│   └── pom.xml
│
├── ms-laboratorios/                   # Microservicio de laboratorios
│   ├── src/main/java/
│   │   └── com/sumativa/ms_laboratorios/
│   │       ├── controller/            # Endpoints REST
│   │       ├── service/               # Lógica de negocio
│   │       ├── repository/            # Acceso a datos
│   │       ├── entity/                # Entidades JPA
│   │       ├── dto/                   # Data Transfer Objects
│   │       └── config/                # Configuración
│   ├── src/main/resources/
│   │   ├── application.yml            # Configuración de Spring
│   │   └── db/migration/              # Scripts Flyway
│   └── pom.xml
│
├── Wallet_fs3/                        # Oracle Cloud Wallet
├── DuocUC_Fullstack3_Microservices.postman_collection.json
└── README.md (este archivo)
```

---

## Credenciales de Prueba

### ms-users

```
Admin:    admin@hospital.cl / admin123
Doctor:   juan.perez@hospital.cl / doctor123
Lab Tech: maria.gonzalez@hospital.cl / lab123
```

### Base de Datos Oracle

```
URL: jdbc:oracle:thin:@fs3_tp?TNS_ADMIN=./wallet
Usuario: ADMIN
Password: Duocuc@.,2025
```

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

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@fs3_tp?TNS_ADMIN=./wallet
    username: ADMIN
    password: Duocuc@.,2025
```

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

## Licencia

Proyecto académico - DuocUC 2025

---

**Desarrollado con:** Spring Boot, Oracle Cloud, Java 21
