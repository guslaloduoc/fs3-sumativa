# Instrucciones para Análisis SonarQube - Frontend

## Preparación ANTES de grabar el video

### 1. Iniciar SonarQube
Abre una terminal y ejecuta:
```bash
# Opción A: Si tienes SonarQube como servicio
# Inicia el servicio de SonarQube

# Opción B: Si usas Docker
docker start sonarqube

# Opción C: Si tienes instalación manual
cd C:\sonarqube\bin\windows-x86-64
StartSonar.bat
```

Espera 1-2 minutos y verifica que SonarQube esté corriendo:
- Abre http://localhost:9000 en tu navegador
- Deberías ver el login de SonarQube

### 2. Verificar que los tests pasen
```bash
cd C:\Users\Gustavo\proyectos\DuocUc\fullstack3\sumativa\frontend
npm run test:headless
```

Deberías ver:
- **TOTAL: 361 SUCCESS**
- **Statements: 98.8%**

---

## DURANTE la grabación del video

### Opción 1: Usar el script automatizado (RECOMENDADO)
```bash
cd C:\Users\Gustavo\proyectos\DuocUc\fullstack3\sumativa\frontend
run-sonar-analysis.cmd
```

El script hará todo automáticamente:
1. ✅ Verificará SonarQube
2. ✅ Ejecutará tests con cobertura
3. ✅ Enviará análisis a SonarQube
4. ✅ Abrirá el dashboard en el navegador

### Opción 2: Comandos manuales
Si prefieres ejecutar paso por paso:

```bash
# Paso 1: Tests con cobertura
npm run test:headless

# Paso 2: Análisis SonarQube
npm run sonar

# Paso 3: Ver resultados
# Abre en navegador: http://localhost:9000/dashboard?id=labcontrol-frontend
```

---

## Qué mostrar en el video

### 1. Ejecución de Tests (30 segundos)
- Terminal mostrando: `npm run test:headless`
- Resultado final: **361 SUCCESS, 98.8% coverage**

### 2. Análisis SonarQube (30 segundos)
- Terminal mostrando: `npm run sonar`
- Mensaje final: "ANALYSIS SUCCESSFUL"

### 3. Dashboard de SonarQube (1-2 minutos)
Navega en http://localhost:9000/dashboard?id=labcontrol-frontend y muestra:

#### Métricas principales:
- ✅ **Passed** - Quality Gate
- ✅ **>90%** Coverage (debería mostrar ~98.8%)
- ✅ **0 Bugs**
- ✅ **0 Vulnerabilities**
- ✅ **Low Code Smells**

#### Detalles de cobertura:
- Haz click en "Coverage" para ver detalles
- Muestra los archivos con alta cobertura:
  - `users.ts` - con tests
  - `results.ts` - con tests
  - `laboratories.ts` - con tests
  - `dashboard.ts` - con tests
  - `forgot-password.ts` - con tests

#### Archivos excluidos (opcional):
- Explica que algunos archivos están excluidos (navbar, footer, sidebar, profile)
- Estos son componentes de UI simple sin lógica de negocio compleja

---

## Resultados Esperados

### Coverage
- **Statements**: ~98.8%
- **Branches**: ~84%
- **Functions**: ~97%
- **Lines**: ~98.75%

### Tests
- **Total**: 361 tests
- **Passed**: 361 (100%)
- **Failed**: 0

### Componentes testeados
1. ✅ **Auth** (Login, Register, Forgot Password)
2. ✅ **Dashboard**
3. ✅ **Laboratories** (CRUD completo)
4. ✅ **Results** (CRUD completo con validaciones)
5. ✅ **Users** (CRUD completo con roles y permisos)

---

## Troubleshooting

### Si SonarQube no está corriendo:
```bash
# Error: "Connection refused" o "ECONNREFUSED"
# Solución: Inicia SonarQube primero
docker start sonarqube
# O ejecuta StartSonar.bat
```

### Si los tests fallan:
```bash
# Vuelve a ejecutar para verificar
npm run test:headless

# Si hay errores, verifica que node_modules esté actualizado
npm install
```

### Si el análisis falla:
```bash
# Verifica que el archivo lcov.info exista
dir coverage\labcontrol-frontend\lcov.info

# Si no existe, ejecuta los tests primero
npm run test:headless
npm run sonar
```

---

## Tips para el video

1. **Graba en resolución HD** (1920x1080) para que el texto sea legible
2. **Muestra la terminal completa** cuando ejecutes los comandos
3. **Espera a que termine cada comando** antes de pasar al siguiente
4. **Haz zoom en el navegador** (Ctrl + +) para que las métricas de SonarQube se vean claramente
5. **Navega por las pestañas** de SonarQube para mostrar diferentes métricas
6. **No edites el video** - muestra el proceso completo en tiempo real

---

## Duración estimada
- Ejecución tests: 1-2 minutos
- Análisis SonarQube: 30-60 segundos
- Mostrar resultados: 1-2 minutos
- **Total**: 3-5 minutos
