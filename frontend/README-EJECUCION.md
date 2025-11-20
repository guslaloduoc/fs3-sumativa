# Lab Manager - Frontend Angular 18

## Descripción

Aplicación frontend desarrollada en Angular 18 con Bootstrap 5 para el sistema de gestión de laboratorios. Se conecta a los microservicios Spring Boot (ms-users y ms-laboratorios).

## Tecnologías

- **Angular**: 18 (standalone components)
- **Bootstrap**: 5.3
- **Bootstrap Icons**: 1.11
- **TypeScript**: 5.x
- **SASS**: Para estilos personalizados
- **RxJS**: Programación reactiva

## Estructura del Proyecto

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/                 # Módulo core
│   │   │   ├── guards/          # Guards de autenticación
│   │   │   ├── interceptors/    # HTTP interceptors
│   │   │   ├── models/          # Interfaces y tipos
│   │   │   └── services/        # Servicios globales
│   │   ├── shared/              # Componentes compartidos
│   │   │   └── components/      # Navbar, Footer, Sidebar
│   │   ├── features/            # Módulos por funcionalidad
│   │   │   ├── auth/           # Login, Register, Forgot Password
│   │   │   ├── dashboard/      # Dashboard principal
│   │   │   ├── profile/        # Perfil de usuario
│   │   │   ├── users/          # Gestión de usuarios (CRUD)
│   │   │   ├── laboratories/   # Gestión de laboratorios (CRUD)
│   │   │   └── results/        # Gestión de resultados (CRUD)
│   │   ├── app.ts              # Componente raíz
│   │   ├── app.routes.ts       # Configuración de rutas
│   │   └── app.config.ts       # Configuración de la app
│   ├── styles.scss             # Estilos globales
│   └── index.html              # HTML principal
└── angular.json                # Configuración de Angular
```

## Requisitos Previos

- Node.js 20+ instalado
- npm 10+ instalado
- Microservicios backend ejecutándose:
  - ms-users en http://localhost:8081
  - ms-laboratorios en http://localhost:8082

## Instalación

1. Navegar al directorio del frontend:
```bash
cd sumativa/frontend
```

2. Instalar dependencias:
```bash
npm install
```

## Ejecución

### Modo desarrollo

```bash
npm start
# o
ng serve
```

La aplicación estará disponible en: http://localhost:4200

### Modo producción

```bash
npm run build
# o
ng build --configuration production
```

Los archivos de producción se generarán en `dist/frontend/`.

## Funcionalidades Implementadas

### Módulo Core
- **AuthService**: Gestión de autenticación y sesión de usuario
- **UserService**: CRUD de usuarios
- **LaboratoryService**: CRUD de laboratorios
- **ResultService**: CRUD de resultados y tipos de análisis
- **AuthGuard**: Protección de rutas autenticadas
- **RoleGuard**: Protección de rutas por rol
- **AuthInterceptor**: Interceptor HTTP (preparado para JWT)

### Módulo Shared
- **Navbar**: Barra de navegación responsiva con Bootstrap
- **Footer**: Pie de página con información del sistema
- **Sidebar**: Componente preparado para navegación lateral

### Módulo Auth
- **Login**: Formulario de inicio de sesión con validación
- **Register**: Página informativa (el registro se hace desde admin)
- **Forgot Password**: Página informativa (recuperación vía admin)

### Módulo Dashboard
- Dashboard con tarjetas informativas
- Información del usuario actual
- Enlaces rápidos a secciones principales

### Routing
- **Lazy Loading**: Carga diferida de módulos
- **Guards**: Protección de rutas autenticadas
- **Redirects**: Redirecciones automáticas

## Rutas Principales

| Ruta | Descripción | Requiere Auth |
|------|-------------|---------------|
| `/` | Redirecciona a /dashboard | No |
| `/auth/login` | Inicio de sesión | No |
| `/auth/register` | Información de registro | No |
| `/auth/forgot-password` | Recuperación de contraseña | No |
| `/dashboard` | Panel principal | Sí |
| `/profile` | Perfil de usuario | Sí |
| `/users` | Gestión de usuarios | Sí |
| `/laboratories` | Gestión de laboratorios | Sí |
| `/results` | Gestión de resultados | Sí |

## Credenciales de Prueba

Usar las credenciales configuradas en el microservicio ms-users (DataInitializer):
- Email: admin@duocuc.cl
- Password: admin123

## Conexión con Backend

Los servicios están configurados para conectarse a:

- **UserService**: `http://localhost:8081/api/users`
- **LaboratoryService**: `http://localhost:8082/api/laboratories`
- **ResultService**: `http://localhost:8082/api/results`

Si los microservicios están en otros puertos, modificar las URLs en los archivos de servicio correspondientes.

## Próximos Pasos (Para completar)

1. **Implementar CRUD completo de Users**:
   - Componente de listado con tabla responsiva
   - Modal/formulario para crear usuario
   - Modal/formulario para editar usuario
   - Confirmación para eliminar usuario
   - Asignación/remoción de roles

2. **Implementar CRUD de Laboratories**:
   - Similar a Users pero más simple
   - Campos: nombre, ubicación, descripción

3. **Implementar CRUD de Results**:
   - Formularios con selects para laboratorio y tipo de análisis
   - Tabla con filtros y búsqueda
   - Campos: paciente, tipo de análisis, laboratorio, resultado, fecha

4. **Implementar Profile**:
   - Mostrar datos del usuario actual
   - Permitir editar nombre y email
   - Cambio de contraseña

5. **Mejoras adicionales**:
   - Paginación en las tablas
   - Filtros y búsqueda
   - Validaciones más robustas
   - Manejo de errores mejorado
   - Indicadores de carga (spinners)
   - Mensajes toast de éxito/error
   - Tests unitarios

## Estructura de Componentes Pendientes

### Users Component (ejemplo estructura)
```typescript
users/
├── users.ts                    // Componente principal con tabla
├── users.html                  // Template con tabla Bootstrap
├── user-form/                  // Subcomponente para formulario
│   ├── user-form.ts
│   └── user-form.html
└── users.scss
```

## Scripts Disponibles

```bash
# Desarrollo
npm start                # Servidor de desarrollo
npm run build            # Build de producción
npm test                # Ejecutar tests
npm run lint            # Linter

# Angular CLI
ng generate component   # Generar componente
ng generate service     # Generar servicio
ng generate guard       # Generar guard
```

## Soporte

Para problemas o preguntas sobre la aplicación, revisar:
1. La consola del navegador para errores de frontend
2. La consola de Angular CLI para errores de compilación
3. Verificar que los microservicios estén ejecutándose
4. Verificar la configuración de CORS en los microservicios

## Notas Importantes

- La aplicación usa **localStorage** para mantener la sesión del usuario
- No hay implementación de JWT en esta versión (autenticación simple)
- Las contraseñas se envían en texto plano según requerimientos académicos
- Bootstrap 5 se importa completo vía SASS para máxima personalización
- Los componentes son **standalone** (sin NgModules)
- Se usa la nueva sintaxis de control flow (`@if`, `@for`) de Angular 18
