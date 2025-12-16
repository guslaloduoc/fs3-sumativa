import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Laboratories } from './laboratories';
import { LaboratoryService } from '../../core/services/laboratory.service';
import { AuthService } from '../../core/services/auth.service';
import { Laboratory } from '../../core/models/laboratory.model';

describe('Laboratories', () => {
  let component: Laboratories;
  let fixture: ComponentFixture<Laboratories>;
  let labServiceSpy: jasmine.SpyObj<LaboratoryService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockLaboratories: Laboratory[] = [
    {
      id: 1,
      nombre: 'Lab 1',
      direccion: 'Address 1',
      telefono: '123456789'
    },
    {
      id: 2,
      nombre: 'Lab 2',
      direccion: 'Address 2',
      telefono: '987654321'
    }
  ];

  beforeEach(async () => {
    const labSpy = jasmine.createSpyObj('LaboratoryService', ['getAll', 'create', 'update', 'delete']);
    const authSpy = jasmine.createSpyObj('AuthService', ['hasRole']);

    await TestBed.configureTestingModule({
      imports: [Laboratories, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        { provide: LaboratoryService, useValue: labSpy },
        { provide: AuthService, useValue: authSpy }
      ]
    }).compileComponents();

    labServiceSpy = TestBed.inject(LaboratoryService) as jasmine.SpyObj<LaboratoryService>;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;

    fixture = TestBed.createComponent(Laboratories);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Initialization', () => {
    it('should initialize with empty laboratories array', () => {
      expect(component.laboratories).toEqual([]);
    });

    it('should initialize form with empty fields', () => {
      expect(component.labForm.get('nombre')?.value).toBe('');
      expect(component.labForm.get('direccion')?.value).toBe('');
      expect(component.labForm.get('telefono')?.value).toBe('');
    });

    it('should have form controls defined', () => {
      expect(component.labForm.contains('nombre')).toBe(true);
      expect(component.labForm.contains('direccion')).toBe(true);
      expect(component.labForm.contains('telefono')).toBe(true);
    });

    it('should initialize modal flags as false', () => {
      expect(component.showFormModal).toBe(false);
      expect(component.showDeleteModal).toBe(false);
      expect(component.isEditMode).toBe(false);
    });

    it('should load laboratories on init', () => {
      labServiceSpy.getAll.and.returnValue(of(mockLaboratories));

      fixture.detectChanges();

      expect(labServiceSpy.getAll).toHaveBeenCalled();
      expect(component.laboratories).toEqual(mockLaboratories);
    });
  });

  describe('loadLaboratories', () => {
    it('should load laboratories successfully', () => {
      labServiceSpy.getAll.and.returnValue(of(mockLaboratories));

      component.loadLaboratories();

      expect(component.isLoading).toBe(false);
      expect(component.laboratories).toEqual(mockLaboratories);
      expect(component.errorMessage).toBe('');
    });

    it('should handle loading error', () => {
      const error = { error: { error: 'Network error' }, message: 'Failed' };
      labServiceSpy.getAll.and.returnValue(throwError(() => error));

      component.loadLaboratories();

      expect(component.isLoading).toBe(false);
      expect(component.errorMessage).toContain('Error al cargar laboratorios');
    });

    it('should not load if already loading', () => {
      component.isLoading = true;
      labServiceSpy.getAll.and.returnValue(of(mockLaboratories));

      component.loadLaboratories();

      expect(labServiceSpy.getAll).not.toHaveBeenCalled();
    });

    it('should clear error message when loading', () => {
      component.errorMessage = 'Previous error';
      labServiceSpy.getAll.and.returnValue(of(mockLaboratories));

      component.loadLaboratories();

      expect(component.errorMessage).toBe('');
    });
  });

  describe('Form Validation', () => {
    it('should invalidate when nombre is empty', () => {
      component.labForm.patchValue({ nombre: '', direccion: 'Test', telefono: '123' });

      expect(component.labForm.invalid).toBe(true);
      expect(component.nombre?.hasError('required')).toBe(true);
    });

    it('should invalidate when nombre is too short', () => {
      component.labForm.patchValue({ nombre: 'ab', direccion: 'Test', telefono: '123' });

      expect(component.nombre?.hasError('minlength')).toBe(true);
    });

    it('should invalidate when direccion is empty', () => {
      component.labForm.patchValue({ nombre: 'Test Lab', direccion: '', telefono: '123' });

      expect(component.labForm.invalid).toBe(true);
      expect(component.direccion?.hasError('required')).toBe(true);
    });

    it('should invalidate when direccion is too short', () => {
      component.labForm.patchValue({ nombre: 'Test Lab', direccion: 'ab', telefono: '123' });

      expect(component.direccion?.hasError('minlength')).toBe(true);
    });

    it('should be valid with correct nombre and direccion', () => {
      component.labForm.patchValue({
        nombre: 'Test Laboratory',
        direccion: 'Test Address 123',
        telefono: '123456789'
      });

      expect(component.labForm.valid).toBe(true);
    });

    it('should be valid without telefono', () => {
      component.labForm.patchValue({
        nombre: 'Test Laboratory',
        direccion: 'Test Address 123',
        telefono: ''
      });

      expect(component.labForm.valid).toBe(true);
    });
  });

  describe('Form Getters', () => {
    it('should return nombre control', () => {
      expect(component.nombre).toBe(component.labForm.get('nombre'));
    });

    it('should return direccion control', () => {
      expect(component.direccion).toBe(component.labForm.get('direccion'));
    });

    it('should return telefono control', () => {
      expect(component.telefono).toBe(component.labForm.get('telefono'));
    });
  });

  describe('canManage', () => {
    it('should return true when user is ADMIN', () => {
      authServiceSpy.hasRole.and.returnValue(true);

      expect(component.canManage).toBe(true);
      expect(authServiceSpy.hasRole).toHaveBeenCalledWith('ADMIN');
    });

    it('should return false when user is not ADMIN', () => {
      authServiceSpy.hasRole.and.returnValue(false);

      expect(component.canManage).toBe(false);
    });
  });

  describe('Create Modal', () => {
    it('should open create modal with empty form', () => {
      component.labForm.patchValue({ nombre: 'Test', direccion: 'Test', telefono: '123' });

      component.openCreateModal();

      expect(component.showFormModal).toBe(true);
      expect(component.isEditMode).toBe(false);
      expect(component.selectedLabId).toBeNull();
      expect(component.labForm.get('nombre')?.value).toBe(null);
    });

    it('should close create modal and reset form', () => {
      component.showFormModal = true;
      component.labForm.patchValue({ nombre: 'Test', direccion: 'Test', telefono: '123' });

      component.closeFormModal();

      expect(component.showFormModal).toBe(false);
      expect(component.labForm.get('nombre')?.value).toBe(null);
      expect(component.selectedLabId).toBeNull();
    });
  });

  describe('Edit Modal', () => {
    it('should open edit modal with lab data', () => {
      const lab = mockLaboratories[0];

      component.openEditModal(lab);

      expect(component.showFormModal).toBe(true);
      expect(component.isEditMode).toBe(true);
      expect(component.selectedLabId).toBe(lab.id);
      expect(component.labForm.get('nombre')?.value).toBe(lab.nombre);
      expect(component.labForm.get('direccion')?.value).toBe(lab.direccion);
      expect(component.labForm.get('telefono')?.value).toBe(lab.telefono);
    });

    it('should handle lab without telefono', () => {
      const lab: Laboratory = { ...mockLaboratories[0], telefono: undefined };

      component.openEditModal(lab);

      expect(component.labForm.get('telefono')?.value).toBe('');
    });
  });

  describe('saveLaboratory - Create', () => {
    it('should create laboratory successfully', fakeAsync(() => {
      labServiceSpy.create.and.returnValue(of(mockLaboratories[0]));
      labServiceSpy.getAll.and.returnValue(of(mockLaboratories));

      component.labForm.patchValue({
        nombre: 'New Lab',
        direccion: 'New Address',
        telefono: '123456789'
      });

      component.saveLaboratory();

      expect(labServiceSpy.create).toHaveBeenCalledWith({
        nombre: 'New Lab',
        direccion: 'New Address',
        telefono: '123456789'
      });

      tick();

      expect(component.successMessage).toBe('Laboratorio creado correctamente');
      expect(component.showFormModal).toBe(false);

      tick(3000);
      expect(component.successMessage).toBe('');
    }));

    it('should handle create error', () => {
      const error = { error: { error: 'Database error' }, message: 'Failed' };
      labServiceSpy.create.and.returnValue(throwError(() => error));

      component.labForm.patchValue({
        nombre: 'New Lab',
        direccion: 'New Address',
        telefono: '123456789'
      });

      component.saveLaboratory();

      expect(component.errorMessage).toContain('Error al crear');
      expect(component.isLoading).toBe(false);
    });

    it('should not create if form is invalid', () => {
      component.labForm.patchValue({ nombre: '', direccion: '', telefono: '' });

      component.saveLaboratory();

      expect(labServiceSpy.create).not.toHaveBeenCalled();
      expect(component.labForm.get('nombre')?.touched).toBe(true);
      expect(component.labForm.get('direccion')?.touched).toBe(true);
    });
  });

  describe('saveLaboratory - Update', () => {
    it('should update laboratory successfully', fakeAsync(() => {
      labServiceSpy.update.and.returnValue(of(mockLaboratories[0]));
      labServiceSpy.getAll.and.returnValue(of(mockLaboratories));

      component.isEditMode = true;
      component.selectedLabId = 1;
      component.labForm.patchValue({
        nombre: 'Updated Lab',
        direccion: 'Updated Address',
        telefono: '987654321'
      });

      component.saveLaboratory();

      expect(labServiceSpy.update).toHaveBeenCalledWith(1, {
        nombre: 'Updated Lab',
        direccion: 'Updated Address',
        telefono: '987654321'
      });

      tick();

      expect(component.successMessage).toBe('Laboratorio actualizado correctamente');
      expect(component.showFormModal).toBe(false);

      tick(3000);
      expect(component.successMessage).toBe('');
    }));

    it('should handle update error', () => {
      const error = { error: { error: 'Not found' }, message: 'Failed' };
      labServiceSpy.update.and.returnValue(throwError(() => error));

      component.isEditMode = true;
      component.selectedLabId = 1;
      component.labForm.patchValue({
        nombre: 'Updated Lab',
        direccion: 'Updated Address',
        telefono: '987654321'
      });

      component.saveLaboratory();

      expect(component.errorMessage).toContain('Error al actualizar');
      expect(component.isLoading).toBe(false);
    });
  });

  describe('Delete Modal', () => {
    it('should open delete modal with lab data', () => {
      const lab = mockLaboratories[0];

      component.openDeleteModal(lab);

      expect(component.showDeleteModal).toBe(true);
      expect(component.labToDelete).toBe(lab);
    });

    it('should close delete modal and clear lab', () => {
      component.showDeleteModal = true;
      component.labToDelete = mockLaboratories[0];

      component.closeDeleteModal();

      expect(component.showDeleteModal).toBe(false);
      expect(component.labToDelete).toBeNull();
    });
  });

  describe('confirmDelete', () => {
    it('should delete laboratory successfully', fakeAsync(() => {
      labServiceSpy.delete.and.returnValue(of(void 0));
      labServiceSpy.getAll.and.returnValue(of([mockLaboratories[1]]));

      component.labToDelete = mockLaboratories[0];

      component.confirmDelete();

      expect(labServiceSpy.delete).toHaveBeenCalledWith(1);

      tick();

      expect(component.successMessage).toBe('Laboratorio eliminado correctamente');
      expect(component.showDeleteModal).toBe(false);
      expect(component.labToDelete).toBeNull();

      tick(3000);
      expect(component.successMessage).toBe('');
    }));

    it('should handle delete error', () => {
      const error = { error: { error: 'Cannot delete' }, message: 'Failed' };
      labServiceSpy.delete.and.returnValue(throwError(() => error));

      component.labToDelete = mockLaboratories[0];

      component.confirmDelete();

      expect(component.errorMessage).toContain('Error al eliminar');
      expect(component.showDeleteModal).toBe(false);
    });

    it('should not delete if labToDelete is null', () => {
      component.labToDelete = null;

      component.confirmDelete();

      expect(labServiceSpy.delete).not.toHaveBeenCalled();
    });
  });

  describe('Component Lifecycle', () => {
    it('should load laboratories on init', () => {
      labServiceSpy.getAll.and.returnValue(of(mockLaboratories));

      fixture.detectChanges();

      expect(labServiceSpy.getAll).toHaveBeenCalled();
      expect(component.laboratories.length).toBe(2);
    });

    it('should unsubscribe on destroy', () => {
      labServiceSpy.getAll.and.returnValue(of(mockLaboratories));

      fixture.detectChanges();

      spyOn(component['loadSubscription']!, 'unsubscribe');

      component.ngOnDestroy();

      expect(component['loadSubscription']!.unsubscribe).toHaveBeenCalled();
    });

    it('should handle destroy when no subscription', () => {
      expect(() => component.ngOnDestroy()).not.toThrow();
    });
  });

  describe('Loading States', () => {
    it('should set isLoading during operations', () => {
      labServiceSpy.getAll.and.returnValue(of(mockLaboratories));

      component.loadLaboratories();

      expect(component.isLoading).toBe(false);
    });

    it('should reset isLoading after error', () => {
      labServiceSpy.getAll.and.returnValue(throwError(() => new Error('Failed')));

      component.loadLaboratories();

      expect(component.isLoading).toBe(false);
    });
  });

  describe('Edge Cases', () => {
    it('should handle empty laboratories list', () => {
      labServiceSpy.getAll.and.returnValue(of([]));

      component.loadLaboratories();

      expect(component.laboratories).toEqual([]);
      expect(component.errorMessage).toBe('');
    });

    it('should handle null telefono in form', () => {
      component.labForm.patchValue({
        nombre: 'Test Lab',
        direccion: 'Test Address',
        telefono: null
      });

      expect(component.labForm.valid).toBe(true);
    });
  });
});
