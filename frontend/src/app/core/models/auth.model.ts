import { Role } from './role.model';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  id: number;
  fullName: string;
  email: string;
  enabled: boolean;
  createdAt: string;
  roles: Role[];
  token: string;
  tokenType: string;
  message: string;
}
