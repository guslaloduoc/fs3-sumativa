import { Role } from './role.model';

export interface User {
  id: number;
  fullName: string;
  email: string;
  passwordHash?: string;
  enabled: boolean;
  createdAt: string;
  roles: Role[];
}

export interface CreateUserRequest {
  fullName: string;
  email: string;
  passwordHash: string;
  enabled?: boolean;
}

export interface UpdateUserRequest {
  fullName?: string;
  email?: string;
  passwordHash?: string;
  enabled?: boolean;
}
