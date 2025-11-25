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

  // Separate loading states
  loadingResults = false;
  loadingTypes = false;
  loadingLabs = false;

  errorMessage = '';
  successMessage = '';

  showFormModal = false;
  isEditMode = false;
  resultForm: FormGroup;
  selectedResultId: number | null = null;

  showDeleteModal = false;
  resultToDelete: Result | null = null;

  // Computed property for overall loading state
  get isLoading(): boolean {
    return this.loadingResults || this.loadingTypes || this.loadingLabs;
  }

  constructor() {
    this.resultForm = this.fb.group({
      paciente: ['', [Validators.required, Validators.minLength(3)]],
      tipoAnalisisId: [null, [Validators.required]],
      laboratorioId: [null, [Validators.required]],
      valorNumerico: [null],
      valorTexto: [''],
      fechaRealizacion: ['', [Validators.required]],
      estado: ['PENDIENTE', [Validators.required]],
      observaciones: ['']
    });
  }

  ngOnInit() {
    this.loadResults();
    this.loadAnalysisTypes();
    this.loadLaboratories();
  }

  loadResults() {
    this.loadingResults = true;
    this.errorMessage = '';
    console.log('[Results] Iniciando carga de resultados...');

    this.resultService.getAll().subscribe({
      next: (results) => {
        console.log('[Results] Resultados recibidos:', results.length);
        this.results = results;
        this.loadingResults = false;
      },
      error: (error) => {
        console.error('[Results] Error cargando resultados:', error);
        this.errorMessage = 'Error al cargar resultados: ' + (error.error?.error || error.message);
        this.loadingResults = false;
      }
    });
  }

  loadAnalysisTypes() {
    this.loadingTypes = true;
    console.log('[Results] Iniciando carga de tipos de análisis...');

    this.resultService.getAllAnalysisTypes().subscribe({
      next: (types) => {
        console.log('[Results] Tipos de análisis recibidos:', types.length);
        this.analysisTypes = types;
        this.loadingTypes = false;
      },
      error: (error) => {
        console.error('[Results] Error cargando tipos de análisis:', error);
        this.loadingTypes = false;
      }
    });
  }

  loadLaboratories() {
    this.loadingLabs = true;
    console.log('[Results] Iniciando carga de laboratorios...');

    this.labService.getAll().subscribe({
      next: (labs) => {
        console.log('[Results] Laboratorios recibidos:', labs.length);
        this.laboratories = labs;
        this.loadingLabs = false;
      },
      error: (error) => {
        console.error('[Results] Error cargando laboratorios:', error);
        this.loadingLabs = false;
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
    const today = new Date().toISOString();
    this.resultForm.patchValue({
      fechaRealizacion: today,
      estado: 'PENDIENTE'
    });
    this.showFormModal = true;
  }

  openEditModal(result: Result) {
    this.isEditMode = true;
    this.selectedResultId = result.id;
    this.resultForm.patchValue({
      paciente: result.paciente,
      tipoAnalisisId: result.tipoAnalisis.id,
      laboratorioId: result.laboratorioId,
      valorNumerico: result.valorNumerico,
      valorTexto: result.valorTexto,
      fechaRealizacion: result.fechaRealizacion,
      estado: result.estado,
      observaciones: result.observaciones
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

    this.loadingResults = true;
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
          this.loadingResults = false;
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
          this.loadingResults = false;
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
    return this.analysisTypes.find(t => t.id === id)?.nombre || 'N/A';
  }

  getLaboratoryName(id: number): string {
    return this.laboratories.find(l => l.id === id)?.nombre || 'N/A';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CL');
  }

  get paciente() { return this.resultForm.get('paciente'); }
  get tipoAnalisisId() { return this.resultForm.get('tipoAnalisisId'); }
  get laboratorioId() { return this.resultForm.get('laboratorioId'); }
  get valorNumerico() { return this.resultForm.get('valorNumerico'); }
  get valorTexto() { return this.resultForm.get('valorTexto'); }
  get fechaRealizacion() { return this.resultForm.get('fechaRealizacion'); }
  get estado() { return this.resultForm.get('estado'); }
  get observaciones() { return this.resultForm.get('observaciones'); }
}
