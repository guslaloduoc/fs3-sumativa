import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { LaboratoryService } from './laboratory.service';
import { Laboratory, CreateLaboratoryRequest, UpdateLaboratoryRequest } from '../models/laboratory.model';

describe('LaboratoryService', () => {
  let service: LaboratoryService;
  let httpMock: HttpTestingController;
  const API_URL = 'http://localhost:8082/api/laboratorios';

  const mockLaboratory: Laboratory = {
    id: 1,
    nombre: 'Laboratorio Central',
    direccion: 'Av. Principal 123',
    telefono: '123456789'
  };

  const mockLaboratories: Laboratory[] = [
    mockLaboratory,
    {
      id: 2,
      nombre: 'Laboratorio Norte',
      direccion: 'Calle Norte 456',
      telefono: '987654321'
    },
    {
      id: 3,
      nombre: 'Laboratorio Sur',
      direccion: 'Av. Sur 789'
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LaboratoryService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(LaboratoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAll', () => {
    it('should return all laboratories', (done) => {
      service.getAll().subscribe({
        next: (laboratories) => {
          expect(laboratories).toEqual(mockLaboratories);
          expect(laboratories.length).toBe(3);
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      expect(req.request.method).toBe('GET');
      req.flush(mockLaboratories);
    });

    it('should return empty array when no laboratories exist', (done) => {
      service.getAll().subscribe({
        next: (laboratories) => {
          expect(laboratories).toEqual([]);
          expect(laboratories.length).toBe(0);
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
      req.flush('Error fetching laboratories', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('getById', () => {
    it('should return a laboratory by id', (done) => {
      const labId = 1;

      service.getById(labId).subscribe({
        next: (laboratory) => {
          expect(laboratory).toEqual(mockLaboratory);
          expect(laboratory.id).toBe(labId);
          expect(laboratory.nombre).toBe('Laboratorio Central');
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${labId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockLaboratory);
    });

    it('should handle laboratory not found', (done) => {
      const labId = 999;

      service.getById(labId).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${labId}`);
      req.flush('Laboratory not found', { status: 404, statusText: 'Not Found' });
    });

    it('should return laboratory without phone number', (done) => {
      const labWithoutPhone: Laboratory = {
        id: 3,
        nombre: 'Lab Sin Telefono',
        direccion: 'Calle 123'
      };

      service.getById(3).subscribe({
        next: (laboratory) => {
          expect(laboratory.telefono).toBeUndefined();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/3`);
      req.flush(labWithoutPhone);
    });
  });

  describe('create', () => {
    it('should create a new laboratory with all fields', (done) => {
      const newLab: CreateLaboratoryRequest = {
        nombre: 'Nuevo Laboratorio',
        direccion: 'Nueva Direccion 123',
        telefono: '111222333'
      };

      const createdLab: Laboratory = {
        id: 4,
        ...newLab
      };

      service.create(newLab).subscribe({
        next: (laboratory) => {
          expect(laboratory).toEqual(createdLab);
          expect(laboratory.id).toBe(4);
          expect(laboratory.nombre).toBe(newLab.nombre);
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newLab);
      req.flush(createdLab);
    });

    it('should create laboratory without phone number', (done) => {
      const newLab: CreateLaboratoryRequest = {
        nombre: 'Lab Sin Telefono',
        direccion: 'Direccion 456'
      };

      const createdLab: Laboratory = {
        id: 5,
        ...newLab
      };

      service.create(newLab).subscribe({
        next: (laboratory) => {
          expect(laboratory.telefono).toBeUndefined();
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush(createdLab);
    });

    it('should handle validation errors', (done) => {
      const invalidLab: CreateLaboratoryRequest = {
        nombre: '',
        direccion: ''
      };

      service.create(invalidLab).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush('Validation failed', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle duplicate laboratory name', (done) => {
      const duplicateLab: CreateLaboratoryRequest = {
        nombre: 'Laboratorio Central',
        direccion: 'Otra direccion'
      };

      service.create(duplicateLab).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush('Laboratory name already exists', { status: 409, statusText: 'Conflict' });
    });
  });

  describe('update', () => {
    it('should update an existing laboratory', (done) => {
      const labId = 1;
      const updateData: UpdateLaboratoryRequest = {
        nombre: 'Laboratorio Central Actualizado',
        direccion: 'Nueva Av. Principal 456',
        telefono: '999888777'
      };

      const updatedLab: Laboratory = {
        id: labId,
        nombre: updateData.nombre!,
        direccion: updateData.direccion!,
        telefono: updateData.telefono
      };

      service.update(labId, updateData).subscribe({
        next: (laboratory) => {
          expect(laboratory).toEqual(updatedLab);
          expect(laboratory.nombre).toBe(updateData.nombre!);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${labId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateData);
      req.flush(updatedLab);
    });

    it('should handle partial updates', (done) => {
      const labId = 1;
      const updateData: UpdateLaboratoryRequest = {
        telefono: '555444333'
      };

      const updatedLab: Laboratory = {
        ...mockLaboratory,
        telefono: updateData.telefono
      };

      service.update(labId, updateData).subscribe({
        next: (laboratory) => {
          expect(laboratory.telefono).toBe(updateData.telefono);
          expect(laboratory.nombre).toBe(mockLaboratory.nombre);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${labId}`);
      req.flush(updatedLab);
    });

    it('should handle laboratory not found for update', (done) => {
      const labId = 999;
      const updateData: UpdateLaboratoryRequest = {
        nombre: 'Nuevo Nombre'
      };

      service.update(labId, updateData).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${labId}`);
      req.flush('Laboratory not found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle duplicate name on update', (done) => {
      const labId = 1;
      const updateData: UpdateLaboratoryRequest = {
        nombre: 'Laboratorio Norte'
      };

      service.update(labId, updateData).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${labId}`);
      req.flush('Laboratory name already exists', { status: 409, statusText: 'Conflict' });
    });
  });

  describe('delete', () => {
    it('should delete a laboratory', (done) => {
      const labId = 1;

      service.delete(labId).subscribe({
        next: () => {
          expect(true).toBe(true);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${labId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle laboratory not found for deletion', (done) => {
      const labId = 999;

      service.delete(labId).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${labId}`);
      req.flush('Laboratory not found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle deletion of laboratory with assignments', (done) => {
      const labId = 1;

      service.delete(labId).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${labId}`);
      req.flush('Cannot delete laboratory with active assignments', {
        status: 400,
        statusText: 'Bad Request'
      });
    });
  });

  describe('API_URL configuration', () => {
    it('should use correct API endpoint', () => {
      expect(service['API_URL']).toBe('http://localhost:8082/api/laboratorios');
    });
  });
});
