import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Laboratory, CreateLaboratoryRequest, UpdateLaboratoryRequest } from '../models/laboratory.model';

@Injectable({
  providedIn: 'root'
})
export class LaboratoryService {
  private readonly API_URL = 'http://localhost:8082/laboratorios';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Laboratory[]> {
    return this.http.get<Laboratory[]>(this.API_URL);
  }

  getById(id: number): Observable<Laboratory> {
    return this.http.get<Laboratory>(`${this.API_URL}/${id}`);
  }

  create(laboratory: CreateLaboratoryRequest): Observable<Laboratory> {
    return this.http.post<Laboratory>(this.API_URL, laboratory);
  }

  update(id: number, laboratory: UpdateLaboratoryRequest): Observable<Laboratory> {
    return this.http.put<Laboratory>(`${this.API_URL}/${id}`, laboratory);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
