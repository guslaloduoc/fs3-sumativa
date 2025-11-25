import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { LaboratoryService } from '../../core/services/laboratory.service';
import { AuthService } from '../../core/services/auth.service';
import { Laboratory, CreateLaboratoryRequest, UpdateLaboratoryRequest } from '../../core/models/laboratory.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-laboratories',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './laboratories.html',
  styleUrl: './laboratories.scss',
})
export class Laboratories implements OnInit, OnDestroy {
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

  private loadSubscription?: Subscription;

  constructor() {
    this.labForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      direccion: ['', [Validators.required, Validators.minLength(3)]],
      telefono: ['']
    });
  }

  ngOnInit() {
    this.loadLaboratories();
  }

  loadLaboratories() {
    // Si ya hay una petición en progreso, no hacer nada
    if (this.isLoading) {
      console.log('[Laboratories] Ya hay una carga en progreso, ignorando...');
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    console.log('[Laboratories] Iniciando carga de laboratorios...');

    this.loadSubscription = this.labService.getAll().subscribe({
      next: (labs) => {
        console.log('[Laboratories] Datos recibidos:', labs);
        console.log('[Laboratories] Cantidad:', labs.length);
        this.laboratories = labs;
        this.isLoading = false;
        console.log('[Laboratories] isLoading = false, laboratories.length =', this.laboratories.length);
      },
      error: (error) => {
        console.error('[Laboratories] ERROR:', error);
        this.errorMessage = 'Error al cargar laboratorios: ' + (error.error?.error || error.message);
        this.isLoading = false;
      }
    });
  }

  ngOnDestroy() {
    if (this.loadSubscription) {
      this.loadSubscription.unsubscribe();
    }
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
      nombre: lab.nombre,
      direccion: lab.direccion,
      telefono: lab.telefono || ''
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

  get nombre() { return this.labForm.get('nombre'); }
  get direccion() { return this.labForm.get('direccion'); }
  get telefono() { return this.labForm.get('telefono'); }
}
