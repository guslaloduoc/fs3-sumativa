import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Results } from './results';
import { ResultService } from '../../core/services/result.service';
import { LaboratoryService } from '../../core/services/laboratory.service';
import { AuthService } from '../../core/services/auth.service';
import { Result, AnalysisType } from '../../core/models/result.model';
import { Laboratory } from '../../core/models/laboratory.model';

describe('Results', () => {
  let component: Results;
  let fixture: ComponentFixture<Results>;
  let resultServiceSpy: jasmine.SpyObj<ResultService>;
  let labServiceSpy: jasmine.SpyObj<LaboratoryService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockAnalysisTypes: AnalysisType[] = [
    {
      id: 1,
      nombre: 'Hemograma',
      categoria: 'Hematología',
      unidadMedida: 'g/dL',
      valorReferenciaMin: 12,
      valorReferenciaMax: 16,
      activo: true
    },
    {
      id: 2,
      nombre: 'Glucosa',
      categoria: 'Bioquímica',
      unidadMedida: 'mg/dL',
      valorReferenciaMin: 70,
      valorReferenciaMax: 100,
      activo: true
    }
  ];

  const mockLaboratories: Laboratory[] = [
    { id: 1, nombre: 'Lab Central', direccion: 'Address 1', telefono: '123456789' },
    { id: 2, nombre: 'Lab Norte', direccion: 'Address 2', telefono: '987654321' }
  ];

  const mockResults: Result[] = [
    {
      id: 1,
      paciente: 'Juan Pérez',
      fechaRealizacion: '2024-01-15T10:00:00',
      tipoAnalisis: mockAnalysisTypes[0],
      laboratorioId: 1,
      valorNumerico: 14.5,
      estado: 'COMPLETADO',
      observaciones: 'Normal',
      creadoEn: '2024-01-15T10:00:00',
      actualizadoEn: '2024-01-15T10:00:00'
    },
    {
      id: 2,
      paciente: 'María López',
      fechaRealizacion: '2024-01-16T11:00:00',
      tipoAnalisis: mockAnalysisTypes[1],
      laboratorioId: 2,
      valorTexto: 'Negativo',
      estado: 'PENDIENTE',
      creadoEn: '2024-01-16T11:00:00',
      actualizadoEn: '2024-01-16T11:00:00'
    }
  ];

  beforeEach(async () => {
    const resultSpy = jasmine.createSpyObj('ResultService', ['getAll', 'getAllAnalysisTypes', 'create', 'update', 'delete']);
    const labSpy = jasmine.createSpyObj('LaboratoryService', ['getAll']);
    const authSpy = jasmine.createSpyObj('AuthService', ['hasRole']);

    await TestBed.configureTestingModule({
      imports: [Results, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        { provide: ResultService, useValue: resultSpy },
        { provide: LaboratoryService, useValue: labSpy },
        { provide: AuthService, useValue: authSpy }
      ]
    }).compileComponents();

    resultServiceSpy = TestBed.inject(ResultService) as jasmine.SpyObj<ResultService>;
    labServiceSpy = TestBed.inject(LaboratoryService) as jasmine.SpyObj<LaboratoryService>;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;

    fixture = TestBed.createComponent(Results);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Initialization', () => {
    it('should initialize with empty arrays', () => {
      expect(component.results).toEqual([]);
      expect(component.analysisTypes).toEqual([]);
      expect(component.laboratories).toEqual([]);
    });

    it('should initialize form with correct fields', () => {
      expect(component.resultForm.contains('paciente')).toBe(true);
      expect(component.resultForm.contains('tipoAnalisisId')).toBe(true);
      expect(component.resultForm.contains('laboratorioId')).toBe(true);
      expect(component.resultForm.contains('valorNumerico')).toBe(true);
      expect(component.resultForm.contains('valorTexto')).toBe(true);
      expect(component.resultForm.contains('fechaRealizacion')).toBe(true);
      expect(component.resultForm.contains('estado')).toBe(true);
      expect(component.resultForm.contains('observaciones')).toBe(true);
    });

    it('should initialize modal flags as false', () => {
      expect(component.showFormModal).toBe(false);
      expect(component.showDeleteModal).toBe(false);
      expect(component.isEditMode).toBe(false);
    });

    it('should load all data on init', () => {
      resultServiceSpy.getAll.and.returnValue(of(mockResults));
      resultServiceSpy.getAllAnalysisTypes.and.returnValue(of(mockAnalysisTypes));
      labServiceSpy.getAll.and.returnValue(of(mockLaboratories));

      fixture.detectChanges();

      expect(resultServiceSpy.getAll).toHaveBeenCalled();
      expect(resultServiceSpy.getAllAnalysisTypes).toHaveBeenCalled();
      expect(labServiceSpy.getAll).toHaveBeenCalled();
    });
  });

  describe('loadResults', () => {
    it('should load results successfully', () => {
      resultServiceSpy.getAll.and.returnValue(of(mockResults));

      component.loadResults();

      expect(component.loadingResults).toBe(false);
      expect(component.results).toEqual(mockResults);
      expect(component.errorMessage).toBe('');
    });

    it('should handle loading error', () => {
      const error = { error: { error: 'Network error' }, message: 'Failed' };
      resultServiceSpy.getAll.and.returnValue(throwError(() => error));

      component.loadResults();

      expect(component.loadingResults).toBe(false);
      expect(component.errorMessage).toContain('Error al cargar resultados');
    });
  });

  describe('loadAnalysisTypes', () => {
    it('should load analysis types successfully', () => {
      resultServiceSpy.getAllAnalysisTypes.and.returnValue(of(mockAnalysisTypes));

      component.loadAnalysisTypes();

      expect(component.loadingTypes).toBe(false);
      expect(component.analysisTypes).toEqual(mockAnalysisTypes);
    });

    it('should handle loading error', () => {
      const error = new Error('Failed');
      resultServiceSpy.getAllAnalysisTypes.and.returnValue(throwError(() => error));

      component.loadAnalysisTypes();

      expect(component.loadingTypes).toBe(false);
    });
  });

  describe('loadLaboratories', () => {
    it('should load laboratories successfully', () => {
      labServiceSpy.getAll.and.returnValue(of(mockLaboratories));

      component.loadLaboratories();

      expect(component.loadingLabs).toBe(false);
      expect(component.laboratories).toEqual(mockLaboratories);
    });

    it('should handle loading error', () => {
      const error = new Error('Failed');
      labServiceSpy.getAll.and.returnValue(throwError(() => error));

      component.loadLaboratories();

      expect(component.loadingLabs).toBe(false);
    });
  });

  describe('isLoading getter', () => {
    it('should return true when any loading is active', () => {
      component.loadingResults = true;
      expect(component.isLoading).toBe(true);

      component.loadingResults = false;
      component.loadingTypes = true;
      expect(component.isLoading).toBe(true);

      component.loadingTypes = false;
      component.loadingLabs = true;
      expect(component.isLoading).toBe(true);
    });

    it('should return false when nothing is loading', () => {
      component.loadingResults = false;
      component.loadingTypes = false;
      component.loadingLabs = false;
      expect(component.isLoading).toBe(false);
    });
  });

  describe('Form Validation', () => {
    it('should invalidate when paciente is empty', () => {
      component.resultForm.patchValue({ paciente: '' });
      expect(component.paciente?.hasError('required')).toBe(true);
    });

    it('should invalidate when paciente is too short', () => {
      component.resultForm.patchValue({ paciente: 'ab' });
      expect(component.paciente?.hasError('minlength')).toBe(true);
    });

    it('should invalidate when tipoAnalisisId is null', () => {
      component.resultForm.patchValue({ tipoAnalisisId: null });
      expect(component.tipoAnalisisId?.hasError('required')).toBe(true);
    });

    it('should invalidate when laboratorioId is null', () => {
      component.resultForm.patchValue({ laboratorioId: null });
      expect(component.laboratorioId?.hasError('required')).toBe(true);
    });

    it('should invalidate when fechaRealizacion is empty', () => {
      component.resultForm.patchValue({ fechaRealizacion: '' });
      expect(component.fechaRealizacion?.hasError('required')).toBe(true);
    });

    it('should invalidate when estado is empty', () => {
      component.resultForm.patchValue({ estado: '' });
      expect(component.estado?.hasError('required')).toBe(true);
    });

    it('should be valid with minimum required fields', () => {
      component.resultForm.patchValue({
        paciente: 'Juan Pérez',
        tipoAnalisisId: 1,
        laboratorioId: 1,
        fechaRealizacion: '2024-01-15T10:00:00',
        estado: 'PENDIENTE'
      });

      expect(component.resultForm.valid).toBe(true);
    });
  });

  describe('Form Getters', () => {
    it('should return paciente control', () => {
      expect(component.paciente).toBe(component.resultForm.get('paciente'));
    });

    it('should return tipoAnalisisId control', () => {
      expect(component.tipoAnalisisId).toBe(component.resultForm.get('tipoAnalisisId'));
    });

    it('should return laboratorioId control', () => {
      expect(component.laboratorioId).toBe(component.resultForm.get('laboratorioId'));
    });
  });

  describe('canManageResults', () => {
    it('should return true when user is ADMIN', () => {
      authServiceSpy.hasRole.and.returnValue(true);
      expect(component.canManageResults).toBe(true);
    });

    it('should return true when user is LAB_TECH', () => {
      authServiceSpy.hasRole.and.callFake((role: string) => role === 'LAB_TECH');
      expect(component.canManageResults).toBe(true);
    });

    it('should return false when user has no permissions', () => {
      authServiceSpy.hasRole.and.returnValue(false);
      expect(component.canManageResults).toBe(false);
    });
  });

  describe('Create Modal', () => {
    it('should open create modal with default values', () => {
      component.openCreateModal();

      expect(component.showFormModal).toBe(true);
      expect(component.isEditMode).toBe(false);
      expect(component.selectedResultId).toBeNull();
      expect(component.resultForm.get('estado')?.value).toBe('PENDIENTE');
    });

    it('should close create modal and reset form', () => {
      component.showFormModal = true;
      component.resultForm.patchValue({ paciente: 'Test' });

      component.closeFormModal();

      expect(component.showFormModal).toBe(false);
      expect(component.selectedResultId).toBeNull();
    });
  });

  describe('Edit Modal', () => {
    it('should open edit modal with result data', () => {
      const result = mockResults[0];

      component.openEditModal(result);

      expect(component.showFormModal).toBe(true);
      expect(component.isEditMode).toBe(true);
      expect(component.selectedResultId).toBe(result.id);
      expect(component.resultForm.get('paciente')?.value).toBe(result.paciente);
      expect(component.resultForm.get('tipoAnalisisId')?.value).toBe(result.tipoAnalisis.id);
      expect(component.resultForm.get('laboratorioId')?.value).toBe(result.laboratorioId);
    });
  });

  describe('saveResult - Create', () => {
    it('should create result successfully', fakeAsync(() => {
      resultServiceSpy.create.and.returnValue(of(mockResults[0]));
      resultServiceSpy.getAll.and.returnValue(of(mockResults));

      component.resultForm.patchValue({
        paciente: 'New Patient',
        tipoAnalisisId: 1,
        laboratorioId: 1,
        fechaRealizacion: '2024-01-15T10:00:00',
        estado: 'PENDIENTE'
      });

      component.saveResult();

      expect(resultServiceSpy.create).toHaveBeenCalled();

      tick();

      expect(component.successMessage).toBe('Resultado creado correctamente');
      expect(component.showFormModal).toBe(false);

      tick(3000);
      expect(component.successMessage).toBe('');
    }));

    it('should handle create error', () => {
      const error = { error: { error: 'Database error' }, message: 'Failed' };
      resultServiceSpy.create.and.returnValue(throwError(() => error));

      component.resultForm.patchValue({
        paciente: 'New Patient',
        tipoAnalisisId: 1,
        laboratorioId: 1,
        fechaRealizacion: '2024-01-15T10:00:00',
        estado: 'PENDIENTE'
      });

      component.saveResult();

      expect(component.errorMessage).toContain('Error al crear');
      expect(component.loadingResults).toBe(false);
    });

    it('should not create if form is invalid', () => {
      component.resultForm.patchValue({ paciente: '' });

      component.saveResult();

      expect(resultServiceSpy.create).not.toHaveBeenCalled();
      expect(component.paciente?.touched).toBe(true);
    });
  });

  describe('saveResult - Update', () => {
    it('should update result successfully', fakeAsync(() => {
      resultServiceSpy.update.and.returnValue(of(mockResults[0]));
      resultServiceSpy.getAll.and.returnValue(of(mockResults));

      component.isEditMode = true;
      component.selectedResultId = 1;
      component.resultForm.patchValue({
        paciente: 'Updated Patient',
        tipoAnalisisId: 1,
        laboratorioId: 1,
        fechaRealizacion: '2024-01-15T10:00:00',
        estado: 'COMPLETADO'
      });

      component.saveResult();

      expect(resultServiceSpy.update).toHaveBeenCalledWith(1, jasmine.any(Object));

      tick();

      expect(component.successMessage).toBe('Resultado actualizado correctamente');
      expect(component.showFormModal).toBe(false);

      tick(3000);
      expect(component.successMessage).toBe('');
    }));

    it('should handle update error', () => {
      const error = { error: { error: 'Not found' }, message: 'Failed' };
      resultServiceSpy.update.and.returnValue(throwError(() => error));

      component.isEditMode = true;
      component.selectedResultId = 1;
      component.resultForm.patchValue({
        paciente: 'Updated Patient',
        tipoAnalisisId: 1,
        laboratorioId: 1,
        fechaRealizacion: '2024-01-15T10:00:00',
        estado: 'COMPLETADO'
      });

      component.saveResult();

      expect(component.errorMessage).toContain('Error al actualizar');
      expect(component.loadingResults).toBe(false);
    });
  });

  describe('Delete Modal', () => {
    it('should open delete modal with result data', () => {
      const result = mockResults[0];

      component.openDeleteModal(result);

      expect(component.showDeleteModal).toBe(true);
      expect(component.resultToDelete).toBe(result);
    });

    it('should close delete modal and clear result', () => {
      component.showDeleteModal = true;
      component.resultToDelete = mockResults[0];

      component.closeDeleteModal();

      expect(component.showDeleteModal).toBe(false);
      expect(component.resultToDelete).toBeNull();
    });
  });

  describe('confirmDelete', () => {
    it('should delete result successfully', fakeAsync(() => {
      resultServiceSpy.delete.and.returnValue(of(void 0));
      resultServiceSpy.getAll.and.returnValue(of([mockResults[1]]));

      component.resultToDelete = mockResults[0];

      component.confirmDelete();

      expect(resultServiceSpy.delete).toHaveBeenCalledWith(1);

      tick();

      expect(component.successMessage).toBe('Resultado eliminado correctamente');
      expect(component.showDeleteModal).toBe(false);
      expect(component.resultToDelete).toBeNull();

      tick(3000);
      expect(component.successMessage).toBe('');
    }));

    it('should handle delete error', () => {
      const error = { error: { error: 'Cannot delete' }, message: 'Failed' };
      resultServiceSpy.delete.and.returnValue(throwError(() => error));

      component.resultToDelete = mockResults[0];

      component.confirmDelete();

      expect(component.errorMessage).toContain('Error al eliminar');
      expect(component.showDeleteModal).toBe(false);
    });

    it('should not delete if resultToDelete is null', () => {
      component.resultToDelete = null;

      component.confirmDelete();

      expect(resultServiceSpy.delete).not.toHaveBeenCalled();
    });
  });

  describe('Helper Methods', () => {
    beforeEach(() => {
      component.analysisTypes = mockAnalysisTypes;
      component.laboratories = mockLaboratories;
    });

    it('should get analysis type name', () => {
      expect(component.getAnalysisTypeName(1)).toBe('Hemograma');
      expect(component.getAnalysisTypeName(2)).toBe('Glucosa');
    });

    it('should return N/A for unknown analysis type', () => {
      expect(component.getAnalysisTypeName(999)).toBe('N/A');
    });

    it('should get laboratory name', () => {
      expect(component.getLaboratoryName(1)).toBe('Lab Central');
      expect(component.getLaboratoryName(2)).toBe('Lab Norte');
    });

    it('should return N/A for unknown laboratory', () => {
      expect(component.getLaboratoryName(999)).toBe('N/A');
    });

    it('should format date correctly', () => {
      const dateString = '2024-01-15T10:00:00';
      const formatted = component.formatDate(dateString);
      expect(formatted).toMatch(/\d{1,2}[-\/]\d{1,2}[-\/]\d{4}/); // Format: dd/mm/yyyy or dd-mm-yyyy
    });
  });

  describe('Edge Cases', () => {
    it('should handle empty results list', () => {
      resultServiceSpy.getAll.and.returnValue(of([]));

      component.loadResults();

      expect(component.results).toEqual([]);
      expect(component.errorMessage).toBe('');
    });

    it('should handle optional form fields', () => {
      component.resultForm.patchValue({
        paciente: 'Test Patient',
        tipoAnalisisId: 1,
        laboratorioId: 1,
        fechaRealizacion: '2024-01-15T10:00:00',
        estado: 'PENDIENTE',
        valorNumerico: null,
        valorTexto: '',
        observaciones: ''
      });

      expect(component.resultForm.valid).toBe(true);
    });
  });
});
