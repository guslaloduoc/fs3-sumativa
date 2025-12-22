/**
 * Configuración de entorno para producción (Docker)
 * Todas las URLs apuntan al API Gateway
 */
export const environment = {
  production: true,
  apiGatewayUrl: 'http://localhost:8080',
  apiUrls: {
    users: 'http://localhost:8080/api/users',
    laboratorios: 'http://localhost:8080/api/laboratorios',
    resultados: 'http://localhost:8080/api/resultados',
    tiposAnalisis: 'http://localhost:8080/api/tipos-analisis'
  }
};
