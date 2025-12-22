import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse } from '../models/auth.model';
import { User } from '../models/user.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = environment.apiUrls.users;
  private currentUserSignal = signal<User | null>(null);
  private tokenKey = 'auth_token';
  private userKey = 'currentUser';

  readonly currentUser = this.currentUserSignal.asReadonly();

  constructor(private http: HttpClient) {
    this.loadUserFromStorage();
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_URL}/login`, credentials)
      .pipe(
        tap(response => {
          // Almacenar token JWT
          this.setToken(response.token);

          // Convertir LoginResponse a User para mantener compatibilidad
          const user: User = {
            id: response.id,
            fullName: response.fullName,
            email: response.email,
            enabled: response.enabled,
            createdAt: response.createdAt,
            roles: response.roles
          };

          this.setCurrentUser(user);
        })
      );
  }

  logout(): void {
    this.currentUserSignal.set(null);
    localStorage.removeItem(this.userKey);
    localStorage.removeItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return this.currentUserSignal() !== null && this.getToken() !== null;
  }

  hasRole(roleName: string): boolean {
    const user = this.currentUserSignal();
    return user?.roles?.some(role => role.name === roleName) ?? false;
  }

  /**
   * Obtiene el token JWT almacenado
   * @returns Token JWT o null si no existe
   */
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  /**
   * Almacena el token JWT en localStorage
   * @param token Token JWT
   */
  private setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  private setCurrentUser(user: User): void {
    this.currentUserSignal.set(user);
    localStorage.setItem(this.userKey, JSON.stringify(user));
  }

  private loadUserFromStorage(): void {
    const userJson = localStorage.getItem(this.userKey);
    if (userJson && userJson !== 'undefined' && userJson !== 'null') {
      try {
        const user = JSON.parse(userJson);
        this.currentUserSignal.set(user);
      } catch (error) {
        console.error('Error loading user from storage', error);
        localStorage.removeItem(this.userKey);
        localStorage.removeItem(this.tokenKey);
      }
    } else {
      // Limpiar valores inválidos del localStorage
      localStorage.removeItem(this.userKey);
      localStorage.removeItem(this.tokenKey);
    }
  }
}
