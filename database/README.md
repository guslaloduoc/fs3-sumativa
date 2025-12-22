# Scripts de Base de Datos - LabControl

Este directorio contiene los scripts SQL para la inicialización y configuración de las bases de datos del sistema LabControl.

## Estructura

```
database/
├── README.md
├── ms-users/
│   ├── 01_schema.sql      # Esquema de tablas para usuarios y roles
│   └── 02_seed.sql        # Datos iniciales de usuarios y roles
├── ms-laboratorios/
│   ├── 01_schema.sql      # Esquema de tablas para laboratorios
│   └── 02_seed.sql        # Datos iniciales de laboratorios
├── ms-results/
│   ├── 01_schema.sql      # Esquema de tablas para resultados
│   └── 02_seed.sql        # Datos iniciales de tipos de análisis y resultados
├── full_schema.sql        # Esquema completo consolidado
└── full_seed.sql          # Datos iniciales consolidados
```

## Base de Datos

- **Motor**: Oracle Cloud ATP (Autonomous Transaction Processing)
- **Versión**: 19c+
- **Encoding**: UTF-8

## Microservicios y sus Tablas

### ms-users (Puerto 8081)
| Tabla | Descripción |
|-------|-------------|
| `roles` | Roles del sistema (ADMIN, LAB_TECH, DOCTOR) |
| `users` | Usuarios del sistema |
| `user_roles` | Relación Many-to-Many entre usuarios y roles |

### ms-laboratorios (Puerto 8082)
| Tabla | Descripción |
|-------|-------------|
| `laboratorios` | Información de laboratorios clínicos |
| `asignaciones` | Asignaciones de pacientes a laboratorios |

### ms-results (Puerto 8083)
| Tabla | Descripción |
|-------|-------------|
| `tipos_analisis` | Catálogo de tipos de análisis clínicos |
| `resultados` | Resultados de análisis de laboratorio |

## Ejecución de Scripts

### Orden de ejecución para instalación completa:

```sql
-- 1. Ejecutar esquemas en orden
@ms-users/01_schema.sql
@ms-laboratorios/01_schema.sql
@ms-results/01_schema.sql

-- 2. Ejecutar datos iniciales
@ms-users/02_seed.sql
@ms-laboratorios/02_seed.sql
@ms-results/02_seed.sql
```

### O usar los scripts consolidados:

```sql
@full_schema.sql
@full_seed.sql
```

## Usuarios de Prueba

| Email | Contraseña | Rol |
|-------|------------|-----|
| admin@hospital.cl | admin123 | ADMIN |
| maria.gonzalez@hospital.cl | lab123 | LAB_TECH |
| juan.perez@hospital.cl | doctor123 | DOCTOR |

## Notas Importantes

1. Los scripts están diseñados para Oracle Database
2. Se utiliza Flyway para migraciones en los microservicios
3. Los scripts de seed son idempotentes donde es posible
4. Las contraseñas en texto plano son solo para desarrollo/demostración

## Flyway

Cada microservicio tiene sus propias migraciones Flyway en:
```
src/main/resources/db/migration/
├── oracle/    # Scripts específicos para Oracle
├── h2/        # Scripts para H2 (desarrollo/testing)
└── common/    # Scripts comunes (seed data)
```
