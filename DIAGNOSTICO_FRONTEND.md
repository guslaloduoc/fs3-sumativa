# Diagnóstico Frontend - No Muestra Datos

## Síntomas
- Endpoints funcionan correctamente en Postman
- Frontend no muestra datos de laboratorios ni resultados
- No hay error de localStorage visible

## Pasos de Diagnóstico

### 1. Abrir Consola del Navegador
1. Abre http://localhost:4101
2. Presiona **F12** para abrir DevTools
3. Ve a la pestaña **Console**

### 2. Buscar Errores
Busca mensajes de error en rojo. Los más comunes:
- `CORS policy: No 'Access-Control-Allow-Origin'`
- `Failed to fetch`
- `Network request failed`
- `HttpErrorResponse`
- `ERROR Error: Uncaught`

**¿Qué errores ves en la consola?**

### 3. Verificar Peticiones de Red
1. Ve a la pestaña **Network** en DevTools
2. Recarga la página (F5)
3. Navega a "Laboratorios"
4. Busca la petición: `http://localhost:8082/api/laboratorios`

**Pregunta 1**: ¿Aparece la petición en la lista?
- [ ] Sí
- [ ] No

**Pregunta 2**: Si aparece, ¿cuál es el Status Code?
- [ ] 200 (OK)
- [ ] 403 (Forbidden - CORS)
- [ ] 404 (Not Found)
- [ ] 500 (Internal Server Error)
- [ ] Failed (no llegó al servidor)

**Pregunta 3**: Haz click en la petición y ve a la pestaña "Response"
¿Qué muestra?

### 4. Verificar Estado de Autenticación
En la consola del navegador, ejecuta:
```javascript
localStorage.getItem('currentUser')
```

**¿Qué devuelve?**
- [ ] `null`
- [ ] `"undefined"`
- [ ] Un objeto JSON con datos de usuario
- [ ] Otro valor

### 5. Limpiar Caché del Navegador
1. Presiona **Ctrl + Shift + Delete**
2. Selecciona:
   - ✅ Cookies y otros datos de sitios
   - ✅ Imágenes y archivos en caché
3. Click en "Borrar datos"
4. Recarga la página con **Ctrl + F5** (recarga forzada)

### 6. Verificar en Modo Incógnito
1. Abre una ventana de incógnito: **Ctrl + Shift + N**
2. Ve a http://localhost:4101
3. ¿Funciona ahí?

## Respuestas Esperadas

Por favor copia y pega aquí:

1. **Errores en Console**:
```
(pega aquí los errores que ves)
```

2. **Estado de petición a laboratorios**:
   - Aparece: [ ]
   - Status: [ ]
   - Response:
   ```
   (pega aquí la respuesta si aparece)
   ```

3. **localStorage currentUser**:
```
(pega el resultado)
```

4. **¿Funciona en modo incógnito?**: [ ]
