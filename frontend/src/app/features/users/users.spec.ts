import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Users } from './users';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../core/models/user.model';
import { signal } from '@angular/core';

describe('Users', () => {
  let component: Users;
  let fixture: ComponentFixture<Users>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockUsers: User[] = [
    {
      id: 1,
      fullName: 'Admin User',
      email: 'admin@example.com',
      enabled: true,
      roles: [{ id: 1, name: 'ADMIN' }],
      createdAt: '2024-01-01T00:00:00'
    },
    {
      id: 2,
      fullName: 'Lab Tech',
      email: 'tech@example.com',
      enabled: true,
      roles: [{ id: 2, name: 'LAB_TECH' }],
      createdAt: '2024-01-02T00:00:00'
    }
  ];

  const mockCurrentUser = mockUsers[0];

  beforeEach(async () => {
    const userSpy = jasmine.createSpyObj('UserService', ['getAll', 'create', 'update', 'delete', 'assignRole', 'removeRole', 'toggleEnabled']);
    const authSpy = jasmine.createSpyObj('AuthService', ['hasRole'], {
      currentUser: signal(mockCurrentUser)
    });

    await TestBed.configureTestingModule({
      imports: [Users, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        { provide: UserService, useValue: userSpy },
        { provide: AuthService, useValue: authSpy }
      ]
    }).compileComponents();

    userServiceSpy = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;

    fixture = TestBed.createComponent(Users);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Initialization', () => {
    it('should initialize with empty users array', () => {
      expect(component.users).toEqual([]);
    });

    it('should initialize form with correct fields', () => {
      expect(component.userForm.contains('fullName')).toBe(true);
      expect(component.userForm.contains('email')).toBe(true);
      expect(component.userForm.contains('passwordHash')).toBe(true);
      expect(component.userForm.contains('confirmPassword')).toBe(true);
      expect(component.userForm.contains('enabled')).toBe(true);
    });

    it('should initialize modal flags as false', () => {
      expect(component.showFormModal).toBe(false);
      expect(component.showRolesModal).toBe(false);
      expect(component.showDeleteModal).toBe(false);
      expect(component.isEditMode).toBe(false);
    });

    it('should load users on init', () => {
      userServiceSpy.getAll.and.returnValue(of(mockUsers));

      fixture.detectChanges();

      expect(userServiceSpy.getAll).toHaveBeenCalled();
      expect(component.users).toEqual(mockUsers);
    });

    it('should have available roles defined', () => {
      expect(component.availableRoles).toEqual(['ADMIN', 'LAB_TECH', 'DOCTOR']);
    });
  });

  describe('loadUsers', () => {
    it('should load users successfully', () => {
      userServiceSpy.getAll.and.returnValue(of(mockUsers));

      component.loadUsers();

      expect(component.isLoading).toBe(false);
      expect(component.users).toEqual(mockUsers);
      expect(component.errorMessage).toBe('');
    });

    it('should handle loading error', () => {
      const error = { error: { error: 'Network error' }, message: 'Failed' };
      userServiceSpy.getAll.and.returnValue(throwError(() => error));

      component.loadUsers();

      expect(component.isLoading).toBe(false);
      expect(component.errorMessage).toContain('Error al cargar usuarios');
    });
  });

  describe('Permission Getters', () => {
    it('should return true when user is ADMIN', () => {
      authServiceSpy.hasRole.and.returnValue(true);
      expect(component.isAdmin).toBe(true);
    });

    it('should return false when user is not ADMIN', () => {
      authServiceSpy.hasRole.and.returnValue(false);
      expect(component.isAdmin).toBe(false);
    });

    it('should return current user ID', () => {
      expect(component.currentUserId).toBe(1);
    });
  });

  describe('Form Validation', () => {
    it('should invalidate when fullName is empty', () => {
      component.userForm.patchValue({ fullName: '' });
      expect(component.fullName?.hasError('required')).toBe(true);
    });

    it('should invalidate when fullName is too short', () => {
      component.userForm.patchValue({ fullName: 'ab' });
      expect(component.fullName?.hasError('minlength')).toBe(true);
    });

    it('should invalidate when email is empty', () => {
      component.userForm.patchValue({ email: '' });
      expect(component.email?.hasError('required')).toBe(true);
    });

    it('should invalidate when email format is invalid', () => {
      component.userForm.patchValue({ email: 'invalid-email' });
      expect(component.email?.hasError('email')).toBe(true);
    });

    it('should invalidate when passwordHash is too short', () => {
      component.userForm.patchValue({ passwordHash: '12345' });
      expect(component.passwordHash?.hasError('minlength')).toBe(true);
    });

    it('should be valid with correct data', () => {
      component.userForm.patchValue({
        fullName: 'Test User',
        email: 'test@example.com',
        passwordHash: 'password123',
        confirmPassword: 'password123',
        enabled: true
      });

      expect(component.userForm.valid).toBe(true);
    });
  });

  describe('Form Getters', () => {
    it('should return fullName control', () => {
      expect(component.fullName).toBe(component.userForm.get('fullName'));
    });

    it('should return email control', () => {
      expect(component.email).toBe(component.userForm.get('email'));
    });

    it('should return passwordHash control', () => {
      expect(component.passwordHash).toBe(component.userForm.get('passwordHash'));
    });

    it('should return confirmPassword control', () => {
      expect(component.confirmPassword).toBe(component.userForm.get('confirmPassword'));
    });
  });

  describe('Create Modal', () => {
    it('should open create modal with default values', () => {
      component.openCreateModal();

      expect(component.showFormModal).toBe(true);
      expect(component.isEditMode).toBe(false);
      expect(component.selectedUserId).toBeNull();
    });

    it('should set password as required in create mode', () => {
      component.openCreateModal();

      expect(component.userForm.get('passwordHash')?.hasError('required')).toBe(true);
      expect(component.userForm.get('confirmPassword')?.hasError('required')).toBe(true);
    });

    it('should close create modal and reset form', () => {
      component.showFormModal = true;
      component.userForm.patchValue({ fullName: 'Test' });

      component.closeFormModal();

      expect(component.showFormModal).toBe(false);
      expect(component.selectedUserId).toBeNull();
    });
  });

  describe('Edit Modal', () => {
    it('should open edit modal with user data', () => {
      const user = mockUsers[0];

      component.openEditModal(user);

      expect(component.showFormModal).toBe(true);
      expect(component.isEditMode).toBe(true);
      expect(component.selectedUserId).toBe(user.id);
      expect(component.userForm.get('fullName')?.value).toBe(user.fullName);
      expect(component.userForm.get('email')?.value).toBe(user.email);
      expect(component.userForm.get('enabled')?.value).toBe(user.enabled);
    });

    it('should clear password validators in edit mode', () => {
      const user = mockUsers[0];

      component.openEditModal(user);

      expect(component.userForm.get('passwordHash')?.hasError('required')).toBe(false);
      expect(component.userForm.get('confirmPassword')?.hasError('required')).toBe(false);
    });
  });

  describe('saveUser - Create', () => {
    it('should create user successfully', fakeAsync(() => {
      userServiceSpy.create.and.returnValue(of(mockUsers[0]));
      userServiceSpy.getAll.and.returnValue(of(mockUsers));

      component.userForm.patchValue({
        fullName: 'New User',
        email: 'new@example.com',
        passwordHash: 'password123',
        confirmPassword: 'password123',
        enabled: true
      });

      component.saveUser();

      expect(userServiceSpy.create).toHaveBeenCalled();

      tick();

      expect(component.successMessage).toBe('Usuario creado correctamente');
      expect(component.showFormModal).toBe(false);

      tick(3000);
      expect(component.successMessage).toBe('');
    }));

    it('should handle create error', () => {
      const error = { error: { error: 'Database error' }, message: 'Failed' };
      userServiceSpy.create.and.returnValue(throwError(() => error));

      component.userForm.patchValue({
        fullName: 'New User',
        email: 'new@example.com',
        passwordHash: 'password123',
        confirmPassword: 'password123',
        enabled: true
      });

      component.saveUser();

      expect(component.errorMessage).toContain('Error al crear');
      expect(component.isLoading).toBe(false);
    });

    it('should not create if form is invalid', () => {
      component.userForm.patchValue({ fullName: '' });

      component.saveUser();

      expect(userServiceSpy.create).not.toHaveBeenCalled();
      expect(component.fullName?.touched).toBe(true);
    });

    it('should not create if passwords do not match', () => {
      component.userForm.patchValue({
        fullName: 'New User',
        email: 'new@example.com',
        passwordHash: 'password123',
        confirmPassword: 'different',
        enabled: true
      });

      component.saveUser();

      expect(userServiceSpy.create).not.toHaveBeenCalled();
      expect(component.errorMessage).toBe('Las contraseñas no coinciden');
    });
  });

  describe('saveUser - Update', () => {
    it('should update user successfully', fakeAsync(() => {
      userServiceSpy.update.and.returnValue(of(mockUsers[0]));
      userServiceSpy.getAll.and.returnValue(of(mockUsers));

      component.openEditModal(mockUsers[0]);
      component.userForm.patchValue({
        fullName: 'Updated User',
        email: 'updated@example.com',
        enabled: true
      });

      component.saveUser();

      expect(userServiceSpy.update).toHaveBeenCalledWith(1, jasmine.any(Object));

      tick();

      expect(component.successMessage).toBe('Usuario actualizado correctamente');
      expect(component.showFormModal).toBe(false);

      tick(3000);
      expect(component.successMessage).toBe('');
    }));

    it('should update with password if provided', fakeAsync(() => {
      userServiceSpy.update.and.returnValue(of(mockUsers[0]));
      userServiceSpy.getAll.and.returnValue(of(mockUsers));

      component.openEditModal(mockUsers[0]);
      component.userForm.patchValue({
        fullName: 'Updated User',
        email: 'updated@example.com',
        passwordHash: 'newpassword123',
        confirmPassword: 'newpassword123',
        enabled: true
      });

      component.saveUser();

      expect(userServiceSpy.update).toHaveBeenCalledWith(1, jasmine.objectContaining({
        password: 'newpassword123'
      }));

      tick();
    }));

    it('should handle update error', () => {
      const error = { error: { error: 'Not found' }, message: 'Failed' };
      userServiceSpy.update.and.returnValue(throwError(() => error));

      component.openEditModal(mockUsers[0]);
      component.userForm.patchValue({
        fullName: 'Updated User',
        email: 'updated@example.com',
        enabled: true
      });

      component.saveUser();

      expect(component.errorMessage).toContain('Error al actualizar');
      expect(component.isLoading).toBe(false);
    });
  });

  describe('Roles Management', () => {
    it('should open roles modal with user data', () => {
      const user = mockUsers[0];

      component.openRolesModal(user);

      expect(component.showRolesModal).toBe(true);
      expect(component.selectedUser).toBe(user);
    });

    it('should close roles modal and clear user', () => {
      component.showRolesModal = true;
      component.selectedUser = mockUsers[0];

      component.closeRolesModal();

      expect(component.showRolesModal).toBe(false);
      expect(component.selectedUser).toBeNull();
    });

    it('should detect if user has role', () => {
      const user = mockUsers[0];

      expect(component.hasRole(user, 'ADMIN')).toBe(true);
      expect(component.hasRole(user, 'DOCTOR')).toBe(false);
    });

    it('should handle user without roles', () => {
      const user: User = { ...mockUsers[0], roles: [] };

      expect(component.hasRole(user, 'ADMIN')).toBe(false);
    });

    it('should assign role successfully', fakeAsync(() => {
      userServiceSpy.assignRole.and.returnValue(of(mockUsers[1]));
      userServiceSpy.getAll.and.returnValue(of(mockUsers));

      const user = mockUsers[1]; // LAB_TECH user

      component.toggleRole(user, 'ADMIN');

      expect(userServiceSpy.assignRole).toHaveBeenCalledWith(2, 'ADMIN');

      tick();

      expect(component.successMessage).toBe('Rol ADMIN asignado');

      tick(3000);
      expect(component.successMessage).toBe('');
    }));

    it('should remove role successfully', fakeAsync(() => {
      userServiceSpy.removeRole.and.returnValue(of(mockUsers[0]));
      userServiceSpy.getAll.and.returnValue(of(mockUsers));

      const user = mockUsers[0]; // ADMIN user

      component.toggleRole(user, 'ADMIN');

      expect(userServiceSpy.removeRole).toHaveBeenCalledWith(1, 'ADMIN');

      tick();

      expect(component.successMessage).toBe('Rol ADMIN removido');

      tick(3000);
      expect(component.successMessage).toBe('');
    }));

    it('should handle assign role error', () => {
      const error = { error: { error: 'Permission denied' }, message: 'Failed' };
      userServiceSpy.assignRole.and.returnValue(throwError(() => error));

      const user = mockUsers[1];

      component.toggleRole(user, 'ADMIN');

      expect(component.errorMessage).toContain('Error al asignar rol');
    });

    it('should handle remove role error', () => {
      const error = { error: { error: 'Permission denied' }, message: 'Failed' };
      userServiceSpy.removeRole.and.returnValue(throwError(() => error));

      const user = mockUsers[0];

      component.toggleRole(user, 'ADMIN');

      expect(component.errorMessage).toContain('Error al remover rol');
    });
  });

  describe('Toggle Enabled', () => {
    it('should toggle user enabled successfully', fakeAsync(() => {
      userServiceSpy.toggleEnabled.and.returnValue(of(mockUsers[0]));
      userServiceSpy.getAll.and.returnValue(of(mockUsers));

      const user = mockUsers[0];

      component.toggleEnabled(user);

      expect(userServiceSpy.toggleEnabled).toHaveBeenCalledWith(1);

      tick();

      expect(component.successMessage).toContain('Usuario');

      tick(3000);
      expect(component.successMessage).toBe('');
    }));

    it('should handle toggle enabled error', () => {
      const error = { error: { error: 'Cannot disable' }, message: 'Failed' };
      userServiceSpy.toggleEnabled.and.returnValue(throwError(() => error));

      const user = mockUsers[0];

      component.toggleEnabled(user);

      expect(component.errorMessage).toContain('Error al cambiar estado');
    });
  });

  describe('Delete Modal', () => {
    it('should open delete modal with user data', () => {
      const user = mockUsers[1]; // Not current user

      component.openDeleteModal(user);

      expect(component.showDeleteModal).toBe(true);
      expect(component.userToDelete).toBe(user);
    });

    it('should close delete modal and clear user', () => {
      component.showDeleteModal = true;
      component.userToDelete = mockUsers[1];

      component.closeDeleteModal();

      expect(component.showDeleteModal).toBe(false);
      expect(component.userToDelete).toBeNull();
    });
  });

  describe('confirmDelete', () => {
    it('should delete user successfully', fakeAsync(() => {
      userServiceSpy.delete.and.returnValue(of(void 0));
      userServiceSpy.getAll.and.returnValue(of([mockUsers[0]]));

      component.userToDelete = mockUsers[1]; // Not current user

      component.confirmDelete();

      expect(userServiceSpy.delete).toHaveBeenCalledWith(2);

      tick();

      expect(component.successMessage).toBe('Usuario eliminado correctamente');
      expect(component.showDeleteModal).toBe(false);
      expect(component.userToDelete).toBeNull();

      tick(3000);
      expect(component.successMessage).toBe('');
    }));

    it('should handle delete error', () => {
      const error = { error: { error: 'Cannot delete' }, message: 'Failed' };
      userServiceSpy.delete.and.returnValue(throwError(() => error));

      component.userToDelete = mockUsers[1];

      component.confirmDelete();

      expect(component.errorMessage).toContain('Error al eliminar');
      expect(component.showDeleteModal).toBe(false);
    });

    it('should not delete if userToDelete is null', () => {
      component.userToDelete = null;

      component.confirmDelete();

      expect(userServiceSpy.delete).not.toHaveBeenCalled();
    });

    it('should prevent deleting current user', () => {
      component.userToDelete = mockUsers[0]; // Current user (id: 1)

      component.confirmDelete();

      expect(userServiceSpy.delete).not.toHaveBeenCalled();
      expect(component.errorMessage).toBe('No puedes eliminar tu propio usuario');
      expect(component.showDeleteModal).toBe(false);
    });
  });

  describe('Edge Cases', () => {
    it('should handle empty users list', () => {
      userServiceSpy.getAll.and.returnValue(of([]));

      component.loadUsers();

      expect(component.users).toEqual([]);
      expect(component.errorMessage).toBe('');
    });

    it('should handle password mismatch validation', () => {
      component.userForm.patchValue({
        fullName: 'Test User',
        email: 'test@example.com',
        passwordHash: 'password123',
        confirmPassword: 'different123',
        enabled: true
      });

      component.saveUser();

      expect(userServiceSpy.create).not.toHaveBeenCalled();
      expect(component.errorMessage).toBe('Las contraseñas no coinciden');
    });

    it('should allow update without password', fakeAsync(() => {
      userServiceSpy.update.and.returnValue(of(mockUsers[0]));
      userServiceSpy.getAll.and.returnValue(of(mockUsers));

      component.openEditModal(mockUsers[0]);
      component.userForm.patchValue({
        fullName: 'Updated User',
        email: 'updated@example.com',
        enabled: true
      });

      component.saveUser();

      expect(userServiceSpy.update).toHaveBeenCalledWith(1, jasmine.objectContaining({
        fullName: 'Updated User',
        email: 'updated@example.com',
        enabled: true
      }));

      tick();
    }));
  });
});
