export interface AnalysisType {
  id: number;
  nombre: string;
  categoria: string;
  unidadMedida?: string;
  valorReferenciaMin?: number;
  valorReferenciaMax?: number;
  activo: boolean;
}

export interface Result {
  id: number;
  paciente: string;
  fechaRealizacion: string;
  tipoAnalisis: AnalysisType;
  laboratorioId: number;
  valorNumerico?: number;
  valorTexto?: string;
  estado: string;
  observaciones?: string;
  creadoEn: string;
  actualizadoEn: string;
}

export interface CreateResultRequest {
  paciente: string;
  fechaRealizacion: string;
  tipoAnalisisId: number;
  laboratorioId: number;
  valorNumerico?: number;
  valorTexto?: string;
  estado: string;
  observaciones?: string;
}

export interface UpdateResultRequest {
  paciente?: string;
  fechaRealizacion?: string;
  tipoAnalisisId?: number;
  laboratorioId?: number;
  valorNumerico?: number;
  valorTexto?: string;
  estado?: string;
  observaciones?: string;
}
