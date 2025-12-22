import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Interceptor HTTP que agrega el token JWT a todas las peticiones
 * excepto a las rutas públicas (login, register)
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  // Rutas que no requieren token JWT (públicas)
  const publicRoutes = ['/api/users/login', '/api/users/register'];

  // Verificar si la ruta actual es pública
  const isPublicRoute = publicRoutes.some(route => req.url.includes(route));

  // Si es ruta pública, no agregar token
  if (isPublicRoute) {
    return next(req);
  }

  // Obtener token del AuthService
  const token = authService.getToken();

  // Si existe token, agregarlo al header Authorization
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};
