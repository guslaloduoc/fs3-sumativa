# ========================================
# STAGE 1: Build
# ========================================
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración Maven primero (para cache de dependencias)
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Descargar dependencias (esta capa se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación (skip tests para build más rápido)
RUN mvn clean package -DskipTests -B

# ========================================
# STAGE 2: Runtime
# ========================================
FROM eclipse-temurin:21-jre-alpine

# Metadata
LABEL maintainer="DuocUC Fullstack 3"
LABEL description="Microservicio de Resultados - Spring Boot 3.3.4"

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Establecer directorio de trabajo
WORKDIR /app

# Crear directorio para wallet
RUN mkdir -p /app/wallet && chown -R spring:spring /app

# Cambiar a usuario no-root
USER spring:spring

# Copiar el JAR desde stage de build
COPY --from=build /app/target/*.jar app.jar

# Copiar wallet de Oracle Cloud (si existe)
# Nota: El wallet se montará como volumen en docker-compose
# COPY --chown=spring:spring ../Wallet_fs3/* /app/wallet/

# Exponer puerto
EXPOSE 8083

# Variables de entorno por defecto (se pueden sobrescribir en docker-compose)
ENV SPRING_PROFILES_ACTIVE=default
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8083/api/resultados || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
