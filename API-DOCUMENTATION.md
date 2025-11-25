# LabControl API - Documentación Completa

## Descripción General

Sistema de gestión de laboratorios clínicos compuesto por 3 microservicios:

- **MS-Users** (Puerto 8081): Gestión de usuarios y autenticación
- **MS-Laboratorios** (Puerto 8082): Gestión de laboratorios y asignaciones
- **MS-Results** (Puerto 8083): Gestión de tipos de análisis y resultados

## Base URLs

```
MS-Users:        http://localhost:8081
MS-Laboratorios: http://localhost:8082
MS-Results:      http://localhost:8083
```

---

## MS-USERS (Puerto 8081)

### Autenticación

#### POST /api/users/login
Inicio de sesión de usuarios.

**Request Body:**
```json
{
  "email": "admin@hospital.cl",
  "password": "admin123"
}
```

**Response 200:**
```json
{
  "id": 1,
  "fullName": "Administrador Sistema",
  "email": "admin@hospital.cl",
  "enabled": true,
  "createdAt": "2025-11-25T00:13:59.78581",
  "roles": [{"name": "ADMIN"}],
  "message": "Inicio de sesión exitoso"
}
```

**Usuarios de prueba:**
- Admin: `admin@hospital.cl` / `admin123`
- Doctor: `juan.perez@hospital.cl` / `doctor123`
- Lab Tech: `maria.gonzalez@hospital.cl` / `lab123`

---

### CRUD de Usuarios

#### GET /api/users
Lista todos los usuarios.

**Response 200:**
```json
[
  {
    "id": 1,
    "fullName": "Administrador Sistema",
    "email": "admin@hospital.cl",
    "enabled": true,
    "createdAt": "2025-11-25T00:13:59.78581",
    "roles": [{"name": "ADMIN"}]
  }
]
```

#### GET /api/users/{id}
Obtiene un usuario por ID.

**Response 200:**
```json
{
  "id": 1,
  "fullName": "Administrador Sistema",
  "email": "admin@hospital.cl",
  "enabled": true,
  "createdAt": "2025-11-25T00:13:59.78581",
  "roles": [{"name": "ADMIN"}]
}
```

#### GET /api/users/email/{email}
Obtiene un usuario por email.

**URL Example:** `/api/users/email/admin@hospital.cl`

#### POST /api/users
Crea un nuevo usuario.

**Request Body:**
```json
{
  "fullName": "Pedro Martínez",
  "email": "pedro.martinez@hospital.cl",
  "password": "pedro123",
  "enabled": true
}
```

**Response 201:**
```json
{
  "id": 4,
  "fullName": "Pedro Martínez",
  "email": "pedro.martinez@hospital.cl",
  "enabled": true,
  "createdAt": "2025-11-25T10:30:00",
  "roles": []
}
```

#### PUT /api/users/{id}
Actualiza un usuario existente.

**Request Body (todos los campos opcionales):**
```json
{
  "fullName": "Pedro Martínez Updated",
  "email": "pedro.martinez@hospital.cl",
  "password": "newpassword123",
  "enabled": false
}
```

#### DELETE /api/users/{id}
Elimina un usuario.

**Response 200:**
```json
{
  "message": "Usuario eliminado exitosamente"
}
```

#### PATCH /api/users/{id}/toggle-enabled
Habilita/deshabilita un usuario.

**Response 200:** Usuario actualizado con estado enabled invertido

---

### Gestión de Roles

#### POST /api/users/{id}/roles/{roleName}
Asigna un rol a un usuario.

**URL Example:** `/api/users/2/roles/ADMIN`

**Roles disponibles:** `ADMIN`, `DOCTOR`, `LAB_TECH`

**Response 200:** Usuario con roles actualizados

#### DELETE /api/users/{id}/roles/{roleName}
Remueve un rol de un usuario.

**URL Example:** `/api/users/2/roles/ADMIN`

**Response 200:** Usuario con roles actualizados

---

## MS-LABORATORIOS (Puerto 8082)

### Gestión de Laboratorios

#### GET /laboratorios
Lista todos los laboratorios.

**Response 200:**
```json
[
  {
    "id": 1,
    "nombre": "Laboratorio Central",
    "direccion": "Av. Providencia 1234, Santiago",
    "telefono": "+56912345678"
  }
]
```

#### GET /laboratorios/{id}
Obtiene un laboratorio por ID.

#### POST /laboratorios
Crea un nuevo laboratorio.

**Request Body:**
```json
{
  "nombre": "Laboratorio Concepción",
  "direccion": "Av. O'Higgins 456, Concepción",
  "telefono": "+56941234567"
}
```

**Response 201:** Laboratorio creado

#### PUT /laboratorios/{id}
Actualiza un laboratorio.

**Request Body (campos opcionales):**
```json
{
  "nombre": "Laboratorio Central Actualizado",
  "direccion": "Nueva dirección",
  "telefono": "+56912345678"
}
```

#### DELETE /laboratorios/{id}
Elimina un laboratorio.

**Response 200:**
```json
{
  "message": "Laboratorio eliminado exitosamente"
}
```

---

### Gestión de Asignaciones

#### GET /asignaciones
Lista todas las asignaciones de pacientes a laboratorios.

**Response 200:**
```json
[
  {
    "id": 1,
    "paciente": "Juan Pérez",
    "fecha": "2025-11-25",
    "laboratorio": {
      "id": 1,
      "nombre": "Laboratorio Central",
      "direccion": "Av. Providencia 1234, Santiago",
      "telefono": "+56912345678"
    }
  }
]
```

#### GET /asignaciones/{id}
Obtiene una asignación por ID.

#### POST /asignaciones
Crea una nueva asignación.

**Request Body:**
```json
{
  "paciente": "Carlos González",
  "fecha": "2025-11-25",
  "laboratorioId": 1
}
```

**Response 201:** Asignación creada

#### PUT /asignaciones/{id}
Actualiza una asignación.

**Request Body (campos opcionales):**
```json
{
  "paciente": "Carlos González Actualizado",
  "fecha": "2025-11-26",
  "laboratorioId": 2
}
```

#### DELETE /asignaciones/{id}
Elimina una asignación.

**Response 200:**
```json
{
  "message": "Asignación eliminada exitosamente"
}
```

---

## MS-RESULTS (Puerto 8083)

### Gestión de Tipos de Análisis

#### GET /api/tipos-analisis
Lista todos los tipos de análisis.

**Response 200:**
```json
[
  {
    "id": 1,
    "nombre": "Hemograma Completo",
    "categoria": "Hematología",
    "unidadMedida": "células/μL",
    "valorReferenciaMin": 4000.00,
    "valorReferenciaMax": 11000.00,
    "activo": true
  }
]
```

#### GET /api/tipos-analisis/{id}
Obtiene un tipo de análisis por ID.

#### POST /api/tipos-analisis
Crea un nuevo tipo de análisis.

**Request Body:**
```json
{
  "nombre": "PCR COVID-19",
  "categoria": "Virología",
  "unidadMedida": null,
  "valorReferenciaMin": null,
  "valorReferenciaMax": null,
  "activo": true
}
```

**Response 201:** Tipo de análisis creado

**Categorías disponibles:**
- Hematología
- Química Sanguínea
- Microbiología
- Inmunología
- Uroanálisis
- Virología

#### PUT /api/tipos-analisis/{id}
Actualiza un tipo de análisis.

**Request Body (campos opcionales):**
```json
{
  "nombre": "Hemograma Actualizado",
  "categoria": "Hematología",
  "activo": true
}
```

#### DELETE /api/tipos-analisis/{id}
Elimina un tipo de análisis.

**Response 204:** No Content

---

### Gestión de Resultados

#### GET /api/resultados
Lista todos los resultados de análisis.

**Response 200:**
```json
[
  {
    "id": 1,
    "paciente": "Juan Pérez González",
    "fechaRealizacion": "2025-11-23T00:25:26.218166",
    "tipoAnalisis": {
      "id": 1,
      "nombre": "Hemograma Completo",
      "categoria": "Hematología",
      "unidadMedida": "células/μL",
      "valorReferenciaMin": 4000.00,
      "valorReferenciaMax": 11000.00,
      "activo": true
    },
    "laboratorioId": 1,
    "valorNumerico": 8500.00,
    "valorTexto": null,
    "estado": "COMPLETADO",
    "observaciones": "Valores dentro del rango normal",
    "creadoEn": "2025-11-23T00:25:26.218166",
    "actualizadoEn": "2025-11-23T00:25:26.218166"
  }
]
```

#### GET /api/resultados/{id}
Obtiene un resultado por ID.

#### POST /api/resultados
Crea un nuevo resultado.

**Request Body - Resultado Numérico:**
```json
{
  "paciente": "Ana Torres",
  "fechaRealizacion": "2025-11-25T10:30:00",
  "tipoAnalisisId": 1,
  "laboratorioId": 1,
  "valorNumerico": 9500.00,
  "valorTexto": null,
  "estado": "COMPLETADO",
  "observaciones": "Valores normales"
}
```

**Request Body - Resultado Texto:**
```json
{
  "paciente": "Luis Hernández",
  "fechaRealizacion": "2025-11-25T11:00:00",
  "tipoAnalisisId": 5,
  "laboratorioId": 2,
  "valorNumerico": null,
  "valorTexto": "pH: 6.5, Densidad: 1.018, Proteínas: Negativo",
  "estado": "PENDIENTE",
  "observaciones": "Análisis en proceso"
}
```

**Estados disponibles:**
- `PENDIENTE`: Análisis en proceso
- `COMPLETADO`: Análisis finalizado
- `CANCELADO`: Análisis cancelado

**Response 201:** Resultado creado

#### PUT /api/resultados/{id}
Actualiza un resultado.

**Request Body (campos opcionales):**
```json
{
  "estado": "COMPLETADO",
  "valorNumerico": 9800.00,
  "observaciones": "Valores actualizados tras revisión"
}
```

#### DELETE /api/resultados/{id}
Elimina un resultado.

**Response 204:** No Content

---

## Tipos de Análisis Disponibles (Datos de Prueba)

| ID | Nombre | Categoría | Unidad | Ref Min | Ref Max |
|----|--------|-----------|--------|---------|---------|
| 1 | Hemograma Completo | Hematología | células/μL | 4000 | 11000 |
| 2 | Glucosa en Sangre | Química Sanguínea | mg/dL | 70 | 100 |
| 3 | Colesterol Total | Química Sanguínea | mg/dL | 0 | 200 |
| 4 | Creatinina | Química Sanguínea | mg/dL | 0.7 | 1.3 |
| 5 | Examen de Orina Completo | Uroanálisis | - | - | - |

---

## Códigos de Error Comunes

- **200 OK**: Solicitud exitosa
- **201 Created**: Recurso creado exitosamente
- **204 No Content**: Recurso eliminado exitosamente
- **400 Bad Request**: Datos inválidos en el request
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error del servidor

---

## Importar Colección en Postman

1. Abre Postman
2. Click en "Import"
3. Selecciona el archivo `LabControl-API.postman_collection.json`
4. La colección se importará con todos los endpoints organizados

---

## Variables de Entorno (Opcional)

Puedes crear un environment en Postman con estas variables:

```json
{
  "baseUrl_users": "http://localhost:8081",
  "baseUrl_labs": "http://localhost:8082",
  "baseUrl_results": "http://localhost:8083"
}
```

---

## Notas Importantes

- Todos los endpoints soportan CORS desde `http://localhost:4101` y `http://localhost:4200`
- Las fechas se manejan en formato ISO 8601: `YYYY-MM-DDTHH:mm:ss`
- Los passwords se almacenan en texto plano (solo para desarrollo/educación)
- Los servicios usan base de datos H2 en memoria cuando corren con perfil `h2`
