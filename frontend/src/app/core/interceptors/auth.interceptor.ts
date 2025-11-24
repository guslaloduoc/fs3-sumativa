import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Para este proyecto no se usa JWT, pero dejamos el interceptor
  // preparado para futuras mejoras

  // Si en el futuro se implementa JWT, se agregaría aquí:
  // const token = localStorage.getItem('token');
  // if (token) {
  //   req = req.clone({
  //     setHeaders: {
  //       Authorization: `Bearer ${token}`
  //     }
  //   });
  // }

  return next(req);
};
