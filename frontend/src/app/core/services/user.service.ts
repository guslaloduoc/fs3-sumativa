import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, CreateUserRequest, UpdateUserRequest } from '../models/user.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API_URL = environment.apiUrls.users;

  constructor(private http: HttpClient) {}

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(this.API_URL);
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/${id}`);
  }

  getByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/email/${email}`);
  }

  create(user: CreateUserRequest): Observable<User> {
    return this.http.post<User>(this.API_URL, user);
  }

  register(user: CreateUserRequest): Observable<User> {
    return this.http.post<User>(`${this.API_URL}/register`, user);
  }

  update(id: number, user: UpdateUserRequest): Observable<User> {
    return this.http.put<User>(`${this.API_URL}/${id}`, user);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  toggleEnabled(id: number): Observable<User> {
    return this.http.patch<User>(`${this.API_URL}/${id}/toggle-enabled`, {});
  }

  assignRole(userId: number, roleName: string): Observable<User> {
    return this.http.post<User>(`${this.API_URL}/${userId}/roles/${roleName}`, {});
  }

  removeRole(userId: number, roleName: string): Observable<User> {
    return this.http.delete<User>(`${this.API_URL}/${userId}/roles/${roleName}`);
  }
}
