# ========================================
# STAGE 1: Build Angular Application
# ========================================
FROM node:20-alpine AS build

WORKDIR /app

# Copiar archivos de dependencias
COPY package.json package-lock.json ./

# Instalar dependencias
RUN npm ci --silent

# Copiar código fuente
COPY . .

# Build de producción
RUN npm run build -- --configuration production

# ========================================
# STAGE 2: Serve with Nginx
# ========================================
FROM nginx:alpine

# Metadata
LABEL maintainer="DuocUC Fullstack 3"
LABEL description="Frontend Angular - LabControl"

# Copiar build de Angular al directorio de Nginx
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Copiar configuración personalizada de Nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Exponer puerto 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1

# Nginx se inicia automáticamente
CMD ["nginx", "-g", "daemon off;"]
