# Documentación de API - Microservicios DuocUC

Este documento describe la estructura de requests/responses y el manejo de errores de los microservicios.

## 📋 Tabla de Contenidos

1. [ms-users - API de Usuarios](#ms-users---api-de-usuarios)
2. [ms-laboratorios - API de Laboratorios](#ms-laboratorios---api-de-laboratorios)
3. [Estructura de Errores](#estructura-de-errores)
4. [Validaciones de Negocio](#validaciones-de-negocio)

---

## 🔐 ms-users - API de Usuarios

**Base URL:** `http://localhost:8081/api/users`

### Estructura de DTOs

#### UserResponseDto (Respuesta)
```json
{
  "id": 1,
  "fullName": "Juan Pérez",
  "email": "juan@duocuc.cl",
  "enabled": true,
  "createdAt": "2025-11-19T10:30:00",
  "roles": [
    { "name": "ADMIN" },
    { "name": "USER" }
  ]
}
```

**Nota:** El campo `passwordHash` **NUNCA** se expone en las respuestas por seguridad.

#### UserCreateDto (Request para crear)
```json
{
  "fullName": "Juan Pérez",
  "email": "juan@duocuc.cl",
  "password": "password123",
  "enabled": true
}
```

**Validaciones:**
- `fullName`: Obligatorio, máximo 150 caracteres
- `email`: Obligatorio, formato válido, **solo dominios: duocuc.cl, example.com**
- `password`: Obligatorio, mínimo 4 caracteres, máximo 200
- `enabled`: Opcional, default `true`

#### UserUpdateDto (Request para actualizar)
```json
{
  "fullName": "Juan Pérez Updated",
  "email": "juan.perez@duocuc.cl",
  "password": "newPassword456",
  "enabled": false
}
```

**Todos los campos son opcionales.** Solo se actualizan los campos que se envíen (no-null).

### Endpoints Principales

#### 1. Listar todos los usuarios
```http
GET /api/users
```

**Respuesta 200 OK:**
```json
[
  {
    "id": 1,
    "fullName": "Admin User",
    "email": "admin@example.com",
    "enabled": true,
    "createdAt": "2025-11-19T10:00:00",
    "roles": [{ "name": "ADMIN" }]
  }
]
```

#### 2. Crear usuario
```http
POST /api/users
Content-Type: application/json

{
  "fullName": "Nuevo Usuario",
  "email": "nuevo@duocuc.cl",
  "password": "pass123",
  "enabled": true
}
```

**Respuesta 201 Created:**
```json
{
  "id": 5,
  "fullName": "Nuevo Usuario",
  "email": "nuevo@duocuc.cl",
  "enabled": true,
  "createdAt": "2025-11-19T15:30:00",
  "roles": []
}
```

#### 3. Actualizar usuario
```http
PUT /api/users/5
Content-Type: application/json

{
  "fullName": "Usuario Actualizado"
}
```

**Respuesta 200 OK:** (UserResponseDto con los datos actualizados)

#### 4. Login
```http
POST /api/users/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "admin123"
}
```

**Respuesta 200 OK:**
```json
{
  "id": 1,
  "fullName": "Admin User",
  "email": "admin@example.com",
  "enabled": true,
  "createdAt": "2025-11-19T10:00:00",
  "roles": [{ "name": "ADMIN" }],
  "message": "Inicio de sesión exitoso"
}
```

---

## 🧪 ms-laboratorios - API de Laboratorios

**Base URL:** `http://localhost:8082`

### Estructura de DTOs

#### LaboratorioResponseDto
```json
{
  "id": 1,
  "nombre": "Lab Central",
  "direccion": "Calle Principal 123",
  "telefono": "912345678"
}
```

#### LaboratorioCreateDto
```json
{
  "nombre": "Nuevo Laboratorio",
  "direccion": "Calle Nueva 456",
  "telefono": "987654321"
}
```

**Validaciones:**
- `nombre`: Obligatorio, máximo 150 caracteres, **debe ser único**
- `direccion`: Opcional, máximo 250 caracteres
- `telefono`: Opcional, **formato: 7-15 dígitos** (acepta espacios, guiones, paréntesis)

#### AsignacionResponseDto
```json
{
  "id": 1,
  "paciente": "María González",
  "fecha": "2025-11-20",
  "laboratorio": {
    "id": 1,
    "nombre": "Lab Central",
    "direccion": "Calle Principal 123",
    "telefono": "912345678"
  }
}
```

#### AsignacionCreateDto
```json
{
  "paciente": "María González",
  "fecha": "2025-11-20",
  "laboratorioId": 1
}
```

### Endpoints Principales

#### 1. Listar laboratorios
```http
GET /laboratorios
```

**Respuesta 200 OK:** Array de `LaboratorioResponseDto`

#### 2. Crear laboratorio
```http
POST /laboratorios
Content-Type: application/json

{
  "nombre": "Lab Norte",
  "direccion": "Av. Norte 789",
  "telefono": "(91) 234-5678"
}
```

**Respuesta 201 Created:** `LaboratorioResponseDto`

#### 3. Crear asignación
```http
POST /asignaciones
Content-Type: application/json

{
  "paciente": "Pedro Sánchez",
  "fecha": "2025-11-21",
  "laboratorioId": 1
}
```

**Respuesta 201 Created:** `AsignacionResponseDto`

---

## ❌ Estructura de Errores

Todos los errores siguen un formato consistente:

### Error de Validación (400 Bad Request)
```json
{
  "timestamp": "2025-11-19T15:45:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los datos enviados",
  "path": "/api/users",
  "fieldErrors": {
    "email": "El email debe tener un formato válido",
    "fullName": "El nombre completo es obligatorio"
  }
}
```

### Error de Negocio (400 Bad Request)
```json
{
  "timestamp": "2025-11-19T15:45:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Dominio de email no autorizado. Dominios permitidos: duocuc.cl, example.com",
  "path": "/api/users"
}
```

### Recurso No Encontrado (404 Not Found)
```json
{
  "timestamp": "2025-11-19T15:45:00",
  "status": 404,
  "error": "Not Found",
  "message": "Usuario no encontrado con id: 999",
  "path": "/api/users/999"
}
```

### Error del Servidor (500 Internal Server Error)
```json
{
  "timestamp": "2025-11-19T15:45:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Ha ocurrido un error inesperado en el servidor",
  "path": "/api/users"
}
```

---

## ✅ Validaciones de Negocio

### ms-users

#### 1. Validación de Dominio de Email
**Regla:** Solo se permiten emails con dominios `duocuc.cl` o `example.com`

**Ejemplo de error:**
```http
POST /api/users
{
  "email": "usuario@gmail.com",
  ...
}
```

**Respuesta 400:**
```json
{
  "status": 400,
  "message": "Dominio de email no autorizado. Dominios permitidos: duocuc.cl, example.com"
}
```

#### 2. No Eliminar Usuarios ADMIN
**Regla:** Usuarios con rol ADMIN no pueden ser eliminados

**Ejemplo de error:**
```http
DELETE /api/users/1
```

**Respuesta 400:**
```json
{
  "status": 400,
  "message": "No se puede eliminar un usuario con rol ADMIN"
}
```

#### 3. No Deshabilitar ADMIN Principal
**Regla:** El usuario `admin@example.com` no puede ser deshabilitado

**Ejemplo de error:**
```http
PATCH /api/users/1/toggle-enabled
```

**Respuesta 400:**
```json
{
  "status": 400,
  "message": "No se puede deshabilitar el usuario ADMIN principal"
}
```

### ms-laboratorios

#### 1. Nombre Único
**Regla:** No se permiten laboratorios con nombres duplicados (case-insensitive)

**Ejemplo de error:**
```http
POST /laboratorios
{
  "nombre": "Lab Central",
  ...
}
```

**Respuesta 400:**
```json
{
  "status": 400,
  "message": "Ya existe un laboratorio con el nombre: Lab Central"
}
```

#### 2. No Eliminar con Asignaciones Activas
**Regla:** Laboratorios con asignaciones no pueden eliminarse

**Ejemplo de error:**
```http
DELETE /laboratorios/1
```

**Respuesta 400:**
```json
{
  "status": 400,
  "message": "No se puede eliminar el laboratorio porque tiene 5 asignaciones activas"
}
```

#### 3. Validación de Formato de Teléfono
**Regla:** El teléfono debe contener entre 7 y 15 dígitos

**Formatos válidos:**
- `912345678`
- `91-234-5678`
- `(91) 234-5678`
- `+56 9 1234 5678`

**Formato inválido:**
- `12345` (muy corto)
- `abc123def` (contiene letras)

**Ejemplo de error:**
```http
POST /laboratorios
{
  "telefono": "12345",
  ...
}
```

**Respuesta 400:**
```json
{
  "status": 400,
  "message": "Formato de teléfono inválido. Debe contener entre 7 y 15 dígitos"
}
```

---

## 🧪 Ejecutar Tests

### ms-users
```bash
cd sumativa/ms-users
./mvnw.cmd test
```

### ms-laboratorios
```bash
cd sumativa/ms-laboratorios
./mvnw.cmd test
```

### Ver cobertura (JaCoCo)
```bash
./mvnw.cmd clean test jacoco:report
# Abrir: target/site/jacoco/index.html
```

---

## 📚 Referencias

- [Spring Boot Validation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.validation)
- [Spring REST Exception Handling](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
