export interface Laboratory {
  id: number;
  nombre: string;
  direccion: string;
  telefono?: string;
}

export interface CreateLaboratoryRequest {
  nombre: string;
  direccion: string;
  telefono?: string;
}

export interface UpdateLaboratoryRequest {
  nombre?: string;
  direccion?: string;
  telefono?: string;
}
