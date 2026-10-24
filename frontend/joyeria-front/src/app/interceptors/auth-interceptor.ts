import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> => {
  const authService = inject(AuthService);
  const token: string | null = authService.getToken();

  if (req.url.includes('/usuarios/login') || req.url.includes('/auth/login')) {
    return next(req);
  }

  if (token && authService.isTokenExpired(token)) {
    authService.handleSessionExpired();
    return throwError(() => new Error('Token expirado'));
  }

  let authReq = req;
  if (token) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 || error.status === 403) {
        authService.handleSessionExpired();
      }
      return throwError(() => error);
    }),
  );
};
