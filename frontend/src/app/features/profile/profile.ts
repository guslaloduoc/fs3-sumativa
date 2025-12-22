import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile implements OnInit {
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private fb = inject(FormBuilder);

  currentUser: User | null = null;
  profileForm: FormGroup;
  isEditing = false;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  showPassword = false;

  constructor() {
    this.profileForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      newPassword: ['', [Validators.minLength(6)]],
      confirmPassword: ['']
    });
  }

  ngOnInit() {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    this.currentUser = this.authService.currentUser();
    if (this.currentUser) {
      this.profileForm.patchValue({
        fullName: this.currentUser.fullName,
        email: this.currentUser.email
      });
    }
  }

  resetForm() {
    if (this.currentUser) {
      this.profileForm.patchValue({
        fullName: this.currentUser.fullName,
        email: this.currentUser.email,
        newPassword: '',
        confirmPassword: ''
      });
    }
    this.errorMessage = '';
    this.successMessage = '';
  }

  get passwordMismatch(): boolean {
    const newPass = this.profileForm.get('newPassword')?.value;
    const confirmPass = this.profileForm.get('confirmPassword')?.value;
    return newPass && confirmPass && newPass !== confirmPass;
  }

  onSubmit() {
    if (this.profileForm.invalid) {
      Object.keys(this.profileForm.controls).forEach(key => {
        this.profileForm.get(key)?.markAsTouched();
      });
      return;
    }

    if (!this.currentUser) return;

    this.isLoading = true;
    this.errorMessage = '';

    const updateData: any = {
      fullName: this.profileForm.value.fullName,
      email: this.profileForm.value.email
    };

    // Only include password if provided
    if (this.profileForm.value.newPassword) {
      if (this.passwordMismatch) {
        this.errorMessage = 'Las contraseñas no coinciden';
        return;
      }
      updateData.password = this.profileForm.value.newPassword;
    }

    this.userService.update(this.currentUser.id, updateData).subscribe({
      next: (updatedUser) => {
        this.currentUser = updatedUser;
        this.successMessage = 'Perfil actualizado correctamente';
        this.isLoading = false;
        this.profileForm.patchValue({ newPassword: '', confirmPassword: '' });
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Error al actualizar perfil: ' + (error.error?.error || error.message);
        this.isLoading = false;
      }
    });
  }

  get fullName() { return this.profileForm.get('fullName'); }
  get email() { return this.profileForm.get('email'); }
  get newPassword() { return this.profileForm.get('newPassword'); }
  get confirmPassword() { return this.profileForm.get('confirmPassword'); }
}
