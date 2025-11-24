export interface AnalysisType {
  id: number;
  name: string;
  description?: string;
}

export interface Result {
  id: number;
  patientName: string;
  analysisTypeId: number;
  analysisType?: AnalysisType;
  laboratoryId: number;
  resultValue: string;
  resultDate: string;
  createdAt: string;
}

export interface CreateResultRequest {
  patientName: string;
  analysisTypeId: number;
  laboratoryId: number;
  resultValue: string;
  resultDate: string;
}

export interface UpdateResultRequest {
  patientName?: string;
  analysisTypeId?: number;
  laboratoryId?: number;
  resultValue?: string;
  resultDate?: string;
}
