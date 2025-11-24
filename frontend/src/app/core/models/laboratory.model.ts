export interface Laboratory {
  id: number;
  name: string;
  location: string;
  description?: string;
  createdAt: string;
}

export interface CreateLaboratoryRequest {
  name: string;
  location: string;
  description?: string;
}

export interface UpdateLaboratoryRequest {
  name?: string;
  location?: string;
  description?: string;
}
