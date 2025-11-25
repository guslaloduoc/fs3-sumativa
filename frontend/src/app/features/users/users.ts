import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { User, CreateUserRequest, UpdateUserRequest } from '../../core/models/user.model';

@Component({
  selector: 'app-users',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './users.html',
  styleUrl: './users.scss',
})
export class Users implements OnInit {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);

  users: User[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  // Modal de formulario
  showFormModal = false;
  isEditMode = false;
  userForm: FormGroup;
  selectedUserId: number | null = null;

  // Modal de roles
  showRolesModal = false;
  selectedUser: User | null = null;
  availableRoles = ['ADMIN', 'LAB_TECH', 'DOCTOR'];

  // Modal de confirmación
  showDeleteModal = false;
  userToDelete: User | null = null;

  constructor() {
    this.userForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      passwordHash: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      enabled: [true]
    });
  }

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.isLoading = true;
    this.errorMessage = '';

    this.userService.getAll().subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Error al cargar usuarios: ' + (error.error?.error || error.message);
        this.isLoading = false;
      }
    });
  }

  get isAdmin() {
    return this.authService.hasRole('ADMIN');
  }

  get currentUserId() {
    return this.authService.currentUser()?.id;
  }

  // Formulario - Crear/Editar
  openCreateModal() {
    this.isEditMode = false;
    this.selectedUserId = null;
    this.userForm.reset({ enabled: true });

    // En modo crear, password es requerido
    this.userForm.get('passwordHash')?.setValidators([Validators.required, Validators.minLength(6)]);
    this.userForm.get('confirmPassword')?.setValidators([Validators.required]);

    this.showFormModal = true;
  }

  openEditModal(user: User) {
    this.isEditMode = true;
    this.selectedUserId = user.id;

    this.userForm.patchValue({
      fullName: user.fullName,
      email: user.email,
      enabled: user.enabled
    });

    // En modo editar, password es opcional
    this.userForm.get('passwordHash')?.clearValidators();
    this.userForm.get('confirmPassword')?.clearValidators();
    this.userForm.get('passwordHash')?.setValue('');
    this.userForm.get('confirmPassword')?.setValue('');

    this.showFormModal = true;
  }

  closeFormModal() {
    this.showFormModal = false;
    this.userForm.reset();
    this.selectedUserId = null;
  }

  saveUser() {
    if (this.userForm.invalid) {
      Object.keys(this.userForm.controls).forEach(key => {
        this.userForm.get(key)?.markAsTouched();
      });
      return;
    }

    // Validar que las contraseñas coincidan (solo si se está creando o si se ingresó password)
    const password = this.userForm.get('passwordHash')?.value;
    const confirmPassword = this.userForm.get('confirmPassword')?.value;

    if (password && password !== confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    if (this.isEditMode && this.selectedUserId) {
      // Editar
      const updateData: UpdateUserRequest = {
        fullName: this.userForm.get('fullName')?.value,
        email: this.userForm.get('email')?.value,
        enabled: this.userForm.get('enabled')?.value
      };

      // Solo incluir password si se proporcionó uno nuevo
      if (password) {
        updateData.passwordHash = password;
      }

      this.userService.update(this.selectedUserId, updateData).subscribe({
        next: () => {
          this.successMessage = 'Usuario actualizado correctamente';
          this.closeFormModal();
          this.loadUsers();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Error al actualizar: ' + (error.error?.error || error.message);
          this.isLoading = false;
        }
      });
    } else {
      // Crear
      const createData: CreateUserRequest = {
        fullName: this.userForm.get('fullName')?.value,
        email: this.userForm.get('email')?.value,
        passwordHash: password,
        enabled: this.userForm.get('enabled')?.value
      };

      this.userService.create(createData).subscribe({
        next: () => {
          this.successMessage = 'Usuario creado correctamente';
          this.closeFormModal();
          this.loadUsers();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Error al crear: ' + (error.error?.error || error.message);
          this.isLoading = false;
        }
      });
    }
  }

  // Gestión de roles
  openRolesModal(user: User) {
    this.selectedUser = user;
    this.showRolesModal = true;
  }

  closeRolesModal() {
    this.showRolesModal = false;
    this.selectedUser = null;
  }

  hasRole(user: User, roleName: string): boolean {
    return user.roles?.some(r => r.name === roleName) ?? false;
  }

  toggleRole(user: User, roleName: string) {
    if (this.hasRole(user, roleName)) {
      // Quitar rol
      this.userService.removeRole(user.id, roleName).subscribe({
        next: () => {
          this.successMessage = `Rol ${roleName} removido`;
          this.loadUsers();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Error al remover rol: ' + (error.error?.error || error.message);
        }
      });
    } else {
      // Asignar rol
      this.userService.assignRole(user.id, roleName).subscribe({
        next: () => {
          this.successMessage = `Rol ${roleName} asignado`;
          this.loadUsers();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Error al asignar rol: ' + (error.error?.error || error.message);
        }
      });
    }
  }

  // Toggle enabled/disabled
  toggleEnabled(user: User) {
    this.userService.toggleEnabled(user.id).subscribe({
      next: () => {
        this.successMessage = `Usuario ${user.enabled ? 'deshabilitado' : 'habilitado'}`;
        this.loadUsers();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Error al cambiar estado: ' + (error.error?.error || error.message);
      }
    });
  }

  // Eliminar
  openDeleteModal(user: User) {
    this.userToDelete = user;
    this.showDeleteModal = true;
  }

  closeDeleteModal() {
    this.showDeleteModal = false;
    this.userToDelete = null;
  }

  confirmDelete() {
    if (!this.userToDelete) return;

    // No permitir eliminar al usuario actual
    if (this.userToDelete.id === this.currentUserId) {
      this.errorMessage = 'No puedes eliminar tu propio usuario';
      this.closeDeleteModal();
      return;
    }

    this.userService.delete(this.userToDelete.id).subscribe({
      next: () => {
        this.successMessage = 'Usuario eliminado correctamente';
        this.closeDeleteModal();
        this.loadUsers();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Error al eliminar: ' + (error.error?.error || error.message);
        this.closeDeleteModal();
      }
    });
  }

  // Getters para el formulario
  get fullName() { return this.userForm.get('fullName'); }
  get email() { return this.userForm.get('email'); }
  get passwordHash() { return this.userForm.get('passwordHash'); }
  get confirmPassword() { return this.userForm.get('confirmPassword'); }
}
