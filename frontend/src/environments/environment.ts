/**
 * Configuración de entorno para desarrollo
 * Todas las URLs apuntan al API Gateway en localhost:8080
 */
export const environment = {
  production: false,
  apiGatewayUrl: 'http://localhost:8080',
  apiUrls: {
    users: 'http://localhost:8080/api/users',
    laboratorios: 'http://localhost:8080/api/laboratorios',
    resultados: 'http://localhost:8080/api/resultados',
    tiposAnalisis: 'http://localhost:8080/api/tipos-analisis'
  }
};
