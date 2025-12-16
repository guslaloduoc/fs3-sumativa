import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ResultService } from './result.service';
import { Result, CreateResultRequest, UpdateResultRequest, AnalysisType } from '../models/result.model';

describe('ResultService', () => {
  let service: ResultService;
  let httpMock: HttpTestingController;
  const API_URL = 'http://localhost:8083/api/resultados';
  const ANALYSIS_TYPES_URL = 'http://localhost:8083/api/tipos-analisis';

  const mockAnalysisType: AnalysisType = {
    id: 1,
    nombre: 'Hemograma Completo',
    categoria: 'Hematología',
    unidadMedida: 'células/mm³',
    valorReferenciaMin: 4000,
    valorReferenciaMax: 11000,
    activo: true
  };

  const mockResult: Result = {
    id: 1,
    paciente: 'Juan Perez',
    fechaRealizacion: '2024-01-15T10:30:00',
    tipoAnalisis: mockAnalysisType,
    laboratorioId: 1,
    valorNumerico: 7500,
    estado: 'COMPLETADO',
    observaciones: 'Valores normales',
    creadoEn: '2024-01-15T10:00:00',
    actualizadoEn: '2024-01-15T10:30:00'
  };

  const mockResults: Result[] = [
    mockResult,
    {
      id: 2,
      paciente: 'Maria Garcia',
      fechaRealizacion: '2024-01-16T14:00:00',
      tipoAnalisis: {
        id: 2,
        nombre: 'Glucosa',
        categoria: 'Bioquímica',
        unidadMedida: 'mg/dL',
        valorReferenciaMin: 70,
        valorReferenciaMax: 100,
        activo: true
      },
      laboratorioId: 2,
      valorNumerico: 85,
      estado: 'PENDIENTE',
      creadoEn: '2024-01-16T13:30:00',
      actualizadoEn: '2024-01-16T14:00:00'
    }
  ];

  const mockAnalysisTypes: AnalysisType[] = [
    mockAnalysisType,
    {
      id: 2,
      nombre: 'Glucosa',
      categoria: 'Bioquímica',
      unidadMedida: 'mg/dL',
      valorReferenciaMin: 70,
      valorReferenciaMax: 100,
      activo: true
    },
    {
      id: 3,
      nombre: 'Creatinina',
      categoria: 'Bioquímica',
      unidadMedida: 'mg/dL',
      activo: true
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ResultService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ResultService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAll', () => {
    it('should return all results', (done) => {
      service.getAll().subscribe({
        next: (results) => {
          expect(results).toEqual(mockResults);
          expect(results.length).toBe(2);
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      expect(req.request.method).toBe('GET');
      req.flush(mockResults);
    });

    it('should return empty array when no results exist', (done) => {
      service.getAll().subscribe({
        next: (results) => {
          expect(results).toEqual([]);
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush([]);
    });

    it('should handle error response', (done) => {
      service.getAll().subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush('Error fetching results', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('getById', () => {
    it('should return a result by id', (done) => {
      const resultId = 1;

      service.getById(resultId).subscribe({
        next: (result) => {
          expect(result).toEqual(mockResult);
          expect(result.id).toBe(resultId);
          expect(result.paciente).toBe('Juan Perez');
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${resultId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResult);
    });

    it('should handle result not found', (done) => {
      const resultId = 999;

      service.getById(resultId).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${resultId}`);
      req.flush('Result not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('create', () => {
    it('should create a new result with numeric value', (done) => {
      const newResult: CreateResultRequest = {
        paciente: 'Carlos Lopez',
        fechaRealizacion: '2024-01-17T09:00:00',
        tipoAnalisisId: 1,
        laboratorioId: 1,
        valorNumerico: 8000,
        estado: 'PENDIENTE',
        observaciones: 'Análisis de rutina'
      };

      const createdResult: Result = {
        id: 3,
        paciente: newResult.paciente,
        fechaRealizacion: newResult.fechaRealizacion,
        tipoAnalisis: mockAnalysisType,
        laboratorioId: newResult.laboratorioId,
        valorNumerico: newResult.valorNumerico,
        estado: newResult.estado!,
        observaciones: newResult.observaciones,
        creadoEn: '2024-01-17T09:00:00',
        actualizadoEn: '2024-01-17T09:00:00'
      };

      service.create(newResult).subscribe({
        next: (result) => {
          expect(result.id).toBe(3);
          expect(result.paciente).toBe(newResult.paciente);
          expect(result.valorNumerico).toBe(newResult.valorNumerico);
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newResult);
      req.flush(createdResult);
    });

    it('should create result with text value', (done) => {
      const newResult: CreateResultRequest = {
        paciente: 'Ana Martinez',
        fechaRealizacion: '2024-01-17T10:00:00',
        tipoAnalisisId: 2,
        laboratorioId: 1,
        valorTexto: 'Positivo',
        estado: 'COMPLETADO'
      };

      const createdResult: Result = {
        id: 4,
        paciente: newResult.paciente,
        fechaRealizacion: newResult.fechaRealizacion,
        tipoAnalisis: mockAnalysisTypes[1],
        laboratorioId: newResult.laboratorioId,
        valorTexto: newResult.valorTexto,
        estado: newResult.estado!,
        creadoEn: '2024-01-17T10:00:00',
        actualizadoEn: '2024-01-17T10:00:00'
      };

      service.create(newResult).subscribe({
        next: (result) => {
          expect(result.valorTexto).toBe('Positivo');
          expect(result.valorNumerico).toBeUndefined();
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush(createdResult);
    });

    it('should handle validation errors', (done) => {
      const invalidResult: CreateResultRequest = {
        paciente: '',
        fechaRealizacion: '',
        tipoAnalisisId: 0,
        laboratorioId: 0,
        estado: ''
      };

      service.create(invalidResult).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush('Validation failed', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle invalid tipoAnalisisId', (done) => {
      const newResult: CreateResultRequest = {
        paciente: 'Test Patient',
        fechaRealizacion: '2024-01-17T10:00:00',
        tipoAnalisisId: 999,
        laboratorioId: 1,
        estado: 'PENDIENTE'
      };

      service.create(newResult).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush('Analysis type not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('update', () => {
    it('should update an existing result', (done) => {
      const resultId = 1;
      const updateData: UpdateResultRequest = {
        estado: 'VALIDADO',
        observaciones: 'Resultados confirmados',
        valorNumerico: 7600
      };

      const updatedResult: Result = {
        ...mockResult,
        estado: updateData.estado!,
        observaciones: updateData.observaciones,
        valorNumerico: updateData.valorNumerico,
        actualizadoEn: '2024-01-15T11:00:00'
      };

      service.update(resultId, updateData).subscribe({
        next: (result) => {
          expect(result.estado).toBe('VALIDADO');
          expect(result.observaciones).toBe('Resultados confirmados');
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${resultId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateData);
      req.flush(updatedResult);
    });

    it('should handle partial updates', (done) => {
      const resultId = 1;
      const updateData: UpdateResultRequest = {
        estado: 'REVISADO'
      };

      const updatedResult: Result = {
        ...mockResult,
        estado: 'REVISADO',
        actualizadoEn: '2024-01-15T11:30:00'
      };

      service.update(resultId, updateData).subscribe({
        next: (result) => {
          expect(result.estado).toBe('REVISADO');
          expect(result.paciente).toBe(mockResult.paciente);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${resultId}`);
      req.flush(updatedResult);
    });

    it('should handle result not found for update', (done) => {
      const resultId = 999;
      const updateData: UpdateResultRequest = {
        estado: 'COMPLETADO'
      };

      service.update(resultId, updateData).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${resultId}`);
      req.flush('Result not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('delete', () => {
    it('should delete a result', (done) => {
      const resultId = 1;

      service.delete(resultId).subscribe({
        next: () => {
          expect(true).toBe(true);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${resultId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle result not found for deletion', (done) => {
      const resultId = 999;

      service.delete(resultId).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${resultId}`);
      req.flush('Result not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('getAllAnalysisTypes', () => {
    it('should return all analysis types', (done) => {
      service.getAllAnalysisTypes().subscribe({
        next: (types) => {
          expect(types).toEqual(mockAnalysisTypes);
          expect(types.length).toBe(3);
          done();
        }
      });

      const req = httpMock.expectOne(ANALYSIS_TYPES_URL);
      expect(req.request.method).toBe('GET');
      req.flush(mockAnalysisTypes);
    });

    it('should return only active analysis types', (done) => {
      const activeTypes = mockAnalysisTypes.filter(t => t.activo);

      service.getAllAnalysisTypes().subscribe({
        next: (types) => {
          expect(types.every(t => t.activo)).toBe(true);
          done();
        }
      });

      const req = httpMock.expectOne(ANALYSIS_TYPES_URL);
      req.flush(activeTypes);
    });

    it('should handle error fetching analysis types', (done) => {
      service.getAllAnalysisTypes().subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(ANALYSIS_TYPES_URL);
      req.flush('Error fetching types', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('getAnalysisTypeById', () => {
    it('should return an analysis type by id', (done) => {
      const typeId = 1;

      service.getAnalysisTypeById(typeId).subscribe({
        next: (type) => {
          expect(type).toEqual(mockAnalysisType);
          expect(type.id).toBe(typeId);
          expect(type.nombre).toBe('Hemograma Completo');
          done();
        }
      });

      const req = httpMock.expectOne(`${ANALYSIS_TYPES_URL}/${typeId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockAnalysisType);
    });

    it('should return analysis type without reference values', (done) => {
      const typeWithoutRef: AnalysisType = {
        id: 3,
        nombre: 'Examen sin referencias',
        categoria: 'Otro',
        activo: true
      };

      service.getAnalysisTypeById(3).subscribe({
        next: (type) => {
          expect(type.valorReferenciaMin).toBeUndefined();
          expect(type.valorReferenciaMax).toBeUndefined();
          expect(type.unidadMedida).toBeUndefined();
          done();
        }
      });

      const req = httpMock.expectOne(`${ANALYSIS_TYPES_URL}/3`);
      req.flush(typeWithoutRef);
    });

    it('should handle analysis type not found', (done) => {
      const typeId = 999;

      service.getAnalysisTypeById(typeId).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${ANALYSIS_TYPES_URL}/${typeId}`);
      req.flush('Analysis type not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('API_URL configuration', () => {
    it('should use correct results API endpoint', () => {
      expect(service['API_URL']).toBe('http://localhost:8083/api/resultados');
    });

    it('should use correct analysis types API endpoint', () => {
      expect(service['ANALYSIS_TYPES_URL']).toBe('http://localhost:8083/api/tipos-analisis');
    });
  });
});
