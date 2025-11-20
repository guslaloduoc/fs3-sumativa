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

  constructor() {
    this.profileForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit() {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    const authData = this.authService.currentUserValue;
    if (authData && authData.user) {
      this.currentUser = authData.user;
      this.profileForm.patchValue({
        fullName: this.currentUser.fullName,
        email: this.currentUser.email
      });
      this.profileForm.disable();
    }
  }

  enableEditing() {
    this.isEditing = true;
    this.profileForm.enable();
  }

  cancelEditing() {
    this.isEditing = false;
    this.profileForm.disable();
    // Restore original values
    if (this.currentUser) {
      this.profileForm.patchValue({
        fullName: this.currentUser.fullName,
        email: this.currentUser.email
      });
    }
    this.errorMessage = '';
  }

  saveProfile() {
    if (this.profileForm.invalid) {
      Object.keys(this.profileForm.controls).forEach(key => {
        this.profileForm.get(key)?.markAsTouched();
      });
      return;
    }

    if (!this.currentUser) return;

    this.isLoading = true;
    this.errorMessage = '';

    const updateData = {
      fullName: this.profileForm.value.fullName,
      email: this.profileForm.value.email
    };

    this.userService.update(this.currentUser.id, updateData).subscribe({
      next: (updatedUser) => {
        this.currentUser = updatedUser;
        this.successMessage = 'Perfil actualizado correctamente';
        this.isEditing = false;
        this.profileForm.disable();
        this.isLoading = false;

        // Update session storage
        const currentAuthData = this.authService.currentUserValue;
        if (currentAuthData) {
          currentAuthData.user = updatedUser;
          sessionStorage.setItem('currentUser', JSON.stringify(currentAuthData));
          this.authService['currentUserSubject'].next(currentAuthData);
        }

        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Error al actualizar perfil: ' + (error.error?.error || error.message);
        this.isLoading = false;
      }
    });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CL', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  get fullName() { return this.profileForm.get('fullName'); }
  get email() { return this.profileForm.get('email'); }
}
