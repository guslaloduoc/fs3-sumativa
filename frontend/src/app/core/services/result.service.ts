import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Result, CreateResultRequest, UpdateResultRequest, AnalysisType } from '../models/result.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ResultService {
  private readonly API_URL = environment.apiUrls.resultados;
  private readonly ANALYSIS_TYPES_URL = environment.apiUrls.tiposAnalisis;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Result[]> {
    return this.http.get<Result[]>(this.API_URL);
  }

  getById(id: number): Observable<Result> {
    return this.http.get<Result>(`${this.API_URL}/${id}`);
  }

  create(result: CreateResultRequest): Observable<Result> {
    return this.http.post<Result>(this.API_URL, result);
  }

  update(id: number, result: UpdateResultRequest): Observable<Result> {
    return this.http.put<Result>(`${this.API_URL}/${id}`, result);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  getAllAnalysisTypes(): Observable<AnalysisType[]> {
    return this.http.get<AnalysisType[]>(this.ANALYSIS_TYPES_URL);
  }

  getAnalysisTypeById(id: number): Observable<AnalysisType> {
    return this.http.get<AnalysisType>(`${this.ANALYSIS_TYPES_URL}/${id}`);
  }
}
