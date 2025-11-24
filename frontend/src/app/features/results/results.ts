import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ResultService } from '../../core/services/result.service';
import { LaboratoryService } from '../../core/services/laboratory.service';
import { AuthService } from '../../core/services/auth.service';
import { Result, CreateResultRequest, UpdateResultRequest, AnalysisType } from '../../core/models/result.model';
import { Laboratory } from '../../core/models/laboratory.model';

@Component({
  selector: 'app-results',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './results.html',
  styleUrl: './results.scss',
})
export class Results implements OnInit {
  private resultService = inject(ResultService);
  private labService = inject(LaboratoryService);
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);

  results: Result[] = [];
  analysisTypes: AnalysisType[] = [];
  laboratories: Laboratory[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  showFormModal = false;
  isEditMode = false;
  resultForm: FormGroup;
  selectedResultId: number | null = null;

  showDeleteModal = false;
  resultToDelete: Result | null = null;

  constructor() {
    this.resultForm = this.fb.group({
      patientName: ['', [Validators.required, Validators.minLength(3)]],
      analysisTypeId: [null, [Validators.required]],
      laboratoryId: [null, [Validators.required]],
      resultValue: ['', [Validators.required]],
      resultDate: ['', [Validators.required]]
    });
  }

  ngOnInit() {
    this.loadResults();
    this.loadAnalysisTypes();
    this.loadLaboratories();
  }

  loadResults() {
    this.isLoading = true;
    this.errorMessage = '';

    this.resultService.getAll().subscribe({
      next: (results) => {
        this.results = results;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Error al cargar resultados: ' + (error.error?.error || error.message);
        this.isLoading = false;
      }
    });
  }

  loadAnalysisTypes() {
    this.resultService.getAllAnalysisTypes().subscribe({
      next: (types) => {
        this.analysisTypes = types;
      },
      error: (error) => {
        console.error('Error al cargar tipos de análisis:', error);
      }
    });
  }

  loadLaboratories() {
    this.labService.getAll().subscribe({
      next: (labs) => {
        this.laboratories = labs;
      },
      error: (error) => {
        console.error('Error al cargar laboratorios:', error);
      }
    });
  }

  get canManageResults() {
    return this.authService.hasRole('ADMIN') || this.authService.hasRole('LAB_TECH');
  }

  openCreateModal() {
    this.isEditMode = false;
    this.selectedResultId = null;
    this.resultForm.reset();
    // Set current date as default
    const today = new Date().toISOString().split('T')[0];
    this.resultForm.patchValue({ resultDate: today });
    this.showFormModal = true;
  }

  openEditModal(result: Result) {
    this.isEditMode = true;
    this.selectedResultId = result.id;
    // Convert date string to YYYY-MM-DD format for input[type="date"]
    const dateOnly = result.resultDate.split('T')[0];
    this.resultForm.patchValue({
      patientName: result.patientName,
      analysisTypeId: result.analysisTypeId,
      laboratoryId: result.laboratoryId,
      resultValue: result.resultValue,
      resultDate: dateOnly
    });
    this.showFormModal = true;
  }

  closeFormModal() {
    this.showFormModal = false;
    this.resultForm.reset();
    this.selectedResultId = null;
  }

  saveResult() {
    if (this.resultForm.invalid) {
      Object.keys(this.resultForm.controls).forEach(key => {
        this.resultForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const formData = this.resultForm.value;

    if (this.isEditMode && this.selectedResultId) {
      const updateData: UpdateResultRequest = formData;
      this.resultService.update(this.selectedResultId, updateData).subscribe({
        next: () => {
          this.successMessage = 'Resultado actualizado correctamente';
          this.closeFormModal();
          this.loadResults();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Error al actualizar: ' + (error.error?.error || error.message);
          this.isLoading = false;
        }
      });
    } else {
      const createData: CreateResultRequest = formData;
      this.resultService.create(createData).subscribe({
        next: () => {
          this.successMessage = 'Resultado creado correctamente';
          this.closeFormModal();
          this.loadResults();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Error al crear: ' + (error.error?.error || error.message);
          this.isLoading = false;
        }
      });
    }
  }

  openDeleteModal(result: Result) {
    this.resultToDelete = result;
    this.showDeleteModal = true;
  }

  closeDeleteModal() {
    this.showDeleteModal = false;
    this.resultToDelete = null;
  }

  confirmDelete() {
    if (!this.resultToDelete) return;

    this.resultService.delete(this.resultToDelete.id).subscribe({
      next: () => {
        this.successMessage = 'Resultado eliminado correctamente';
        this.closeDeleteModal();
        this.loadResults();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Error al eliminar: ' + (error.error?.error || error.message);
        this.closeDeleteModal();
      }
    });
  }

  getAnalysisTypeName(id: number): string {
    return this.analysisTypes.find(t => t.id === id)?.name || 'N/A';
  }

  getLaboratoryName(id: number): string {
    return this.laboratories.find(l => l.id === id)?.name || 'N/A';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CL');
  }

  get patientName() { return this.resultForm.get('patientName'); }
  get analysisTypeId() { return this.resultForm.get('analysisTypeId'); }
  get laboratoryId() { return this.resultForm.get('laboratoryId'); }
  get resultValue() { return this.resultForm.get('resultValue'); }
  get resultDate() { return this.resultForm.get('resultDate'); }
}
