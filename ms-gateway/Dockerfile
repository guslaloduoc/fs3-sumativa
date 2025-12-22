# Multi-stage build para API Gateway con Spring Cloud Gateway
FROM eclipse-temurin:21-jdk-alpine AS builder

# Directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dar permisos de ejecución a mvnw
RUN chmod +x ./mvnw

# Descargar dependencias (se cachea esta capa)
RUN ./mvnw dependency:go-offline

# Copiar código fuente
COPY src src

# Compilar aplicación
RUN ./mvnw clean package -DskipTests

# Etapa final - Runtime
FROM eclipse-temurin:21-jre-alpine

# Directorio de trabajo
WORKDIR /app

# Copiar JAR desde la etapa de build
COPY --from=builder /app/target/*.jar app.jar

# Puerto por defecto del Gateway
EXPOSE 8080

# Health check usando wget (viene preinstalado en alpine)
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=60s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
