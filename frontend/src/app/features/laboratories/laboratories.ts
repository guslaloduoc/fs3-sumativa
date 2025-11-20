import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { LaboratoryService } from '../../core/services/laboratory.service';
import { AuthService } from '../../core/services/auth.service';
import { Laboratory, CreateLaboratoryRequest, UpdateLaboratoryRequest } from '../../core/models/laboratory.model';

@Component({
  selector: 'app-laboratories',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './laboratories.html',
  styleUrl: './laboratories.scss',
})
export class Laboratories implements OnInit {
  private labService = inject(LaboratoryService);
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);

  laboratories: Laboratory[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  showFormModal = false;
  isEditMode = false;
  labForm: FormGroup;
  selectedLabId: number | null = null;

  showDeleteModal = false;
  labToDelete: Laboratory | null = null;

  constructor() {
    this.labForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      location: ['', [Validators.required, Validators.minLength(3)]],
      description: ['']
    });
  }

  ngOnInit() {
    this.loadLaboratories();
  }

  loadLaboratories() {
    this.isLoading = true;
    this.errorMessage = '';

    this.labService.getAll().subscribe({
      next: (labs) => {
        this.laboratories = labs;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Error al cargar laboratorios: ' + (error.error?.error || error.message);
        this.isLoading = false;
      }
    });
  }

  get canManage() {
    return this.authService.hasRole('ADMIN');
  }

  openCreateModal() {
    this.isEditMode = false;
    this.selectedLabId = null;
    this.labForm.reset();
    this.showFormModal = true;
  }

  openEditModal(lab: Laboratory) {
    this.isEditMode = true;
    this.selectedLabId = lab.id;
    this.labForm.patchValue({
      name: lab.name,
      location: lab.location,
      description: lab.description || ''
    });
    this.showFormModal = true;
  }

  closeFormModal() {
    this.showFormModal = false;
    this.labForm.reset();
    this.selectedLabId = null;
  }

  saveLaboratory() {
    if (this.labForm.invalid) {
      Object.keys(this.labForm.controls).forEach(key => {
        this.labForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const formData = this.labForm.value;

    if (this.isEditMode && this.selectedLabId) {
      const updateData: UpdateLaboratoryRequest = formData;
      this.labService.update(this.selectedLabId, updateData).subscribe({
        next: () => {
          this.successMessage = 'Laboratorio actualizado correctamente';
          this.closeFormModal();
          this.loadLaboratories();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Error al actualizar: ' + (error.error?.error || error.message);
          this.isLoading = false;
        }
      });
    } else {
      const createData: CreateLaboratoryRequest = formData;
      this.labService.create(createData).subscribe({
        next: () => {
          this.successMessage = 'Laboratorio creado correctamente';
          this.closeFormModal();
          this.loadLaboratories();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Error al crear: ' + (error.error?.error || error.message);
          this.isLoading = false;
        }
      });
    }
  }

  openDeleteModal(lab: Laboratory) {
    this.labToDelete = lab;
    this.showDeleteModal = true;
  }

  closeDeleteModal() {
    this.showDeleteModal = false;
    this.labToDelete = null;
  }

  confirmDelete() {
    if (!this.labToDelete) return;

    this.labService.delete(this.labToDelete.id).subscribe({
      next: () => {
        this.successMessage = 'Laboratorio eliminado correctamente';
        this.closeDeleteModal();
        this.loadLaboratories();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Error al eliminar: ' + (error.error?.error || error.message);
        this.closeDeleteModal();
      }
    });
  }

  get name() { return this.labForm.get('name'); }
  get location() { return this.labForm.get('location'); }
  get description() { return this.labForm.get('description'); }
}
