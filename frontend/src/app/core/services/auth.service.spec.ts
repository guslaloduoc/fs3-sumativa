import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { LoginRequest, LoginResponse } from '../models/auth.model';
import { User } from '../models/user.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  const API_URL = 'http://localhost:8080/api/users';

  const mockUser: User = {
    id: 1,
    fullName: 'Test User',
    email: 'test@test.com',
    enabled: true,
    roles: [{ id: 1, name: 'ADMIN' }],
    createdAt: '2024-01-01T00:00:00'
  };

  beforeEach(() => {
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login', () => {
    it('should login successfully and set current user', (done) => {
      const credentials: LoginRequest = {
        email: 'test@test.com',
        password: 'password123'
      };

      const mockResponse: LoginResponse = {
        id: mockUser.id,
        fullName: mockUser.fullName,
        email: mockUser.email,
        enabled: mockUser.enabled,
        roles: mockUser.roles,
        createdAt: mockUser.createdAt,
        token: 'mock-jwt-token',
        tokenType: 'Bearer',
        message: 'Login successful'
      };

      service.login(credentials).subscribe({
        next: (response) => {
          expect(response).toEqual(mockResponse);
          expect(service.isAuthenticated()).toBe(true);
          expect(service.currentUser()).toEqual(mockUser);

          const storedUser = localStorage.getItem('currentUser');
          expect(storedUser).toBeTruthy();
          expect(JSON.parse(storedUser!)).toEqual(mockUser);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(credentials);
      req.flush(mockResponse);
    });

    it('should handle login error', (done) => {
      const credentials: LoginRequest = {
        email: 'test@test.com',
        password: 'wrong-password'
      };

      service.login(credentials).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          expect(service.isAuthenticated()).toBe(false);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/login`);
      req.flush('Invalid credentials', { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('logout', () => {
    it('should clear current user and localStorage', () => {
      // Setup: set a user and token first
      localStorage.setItem('currentUser', JSON.stringify(mockUser));
      localStorage.setItem('auth_token', 'mock-jwt-token');
      service['currentUserSignal'].set(mockUser);

      expect(service.isAuthenticated()).toBe(true);

      // Execute logout
      service.logout();

      // Verify
      expect(service.isAuthenticated()).toBe(false);
      expect(service.currentUser()).toBeNull();
      expect(localStorage.getItem('currentUser')).toBeNull();
    });

    it('should work when no user is logged in', () => {
      expect(service.isAuthenticated()).toBe(false);

      service.logout();

      expect(service.isAuthenticated()).toBe(false);
    });
  });

  describe('isAuthenticated', () => {
    it('should return true when user and token are present', () => {
      service['currentUserSignal'].set(mockUser);
      localStorage.setItem('auth_token', 'mock-jwt-token');
      expect(service.isAuthenticated()).toBe(true);
    });

    it('should return false when user is not logged in', () => {
      service['currentUserSignal'].set(null);
      expect(service.isAuthenticated()).toBe(false);
    });

    it('should return false when token is missing', () => {
      service['currentUserSignal'].set(mockUser);
      localStorage.removeItem('auth_token');
      expect(service.isAuthenticated()).toBe(false);
    });
  });

  describe('hasRole', () => {
    it('should return true when user has the specified role', () => {
      service['currentUserSignal'].set(mockUser);
      expect(service.hasRole('ADMIN')).toBe(true);
    });

    it('should return false when user does not have the specified role', () => {
      service['currentUserSignal'].set(mockUser);
      expect(service.hasRole('LAB_TECH')).toBe(false);
    });

    it('should return false when user is null', () => {
      service['currentUserSignal'].set(null);
      expect(service.hasRole('ADMIN')).toBe(false);
    });

    it('should return false when user has no roles', () => {
      const userWithoutRoles: User = {
        ...mockUser,
        roles: []
      };
      service['currentUserSignal'].set(userWithoutRoles);
      expect(service.hasRole('ADMIN')).toBe(false);
    });

    it('should handle user with multiple roles', () => {
      const userWithMultipleRoles: User = {
        ...mockUser,
        roles: [
          { id: 1, name: 'ADMIN' },
          { id: 2, name: 'LAB_TECH' },
          { id: 3, name: 'DOCTOR' }
        ]
      };
      service['currentUserSignal'].set(userWithMultipleRoles);

      expect(service.hasRole('ADMIN')).toBe(true);
      expect(service.hasRole('LAB_TECH')).toBe(true);
      expect(service.hasRole('DOCTOR')).toBe(true);
      expect(service.hasRole('UNKNOWN')).toBe(false);
    });
  });

  describe('loadUserFromStorage', () => {
    it('should load user from localStorage on service initialization', () => {
      localStorage.setItem('currentUser', JSON.stringify(mockUser));
      localStorage.setItem('auth_token', 'mock-jwt-token');

      // Create new service instance to trigger constructor
      const newService = new AuthService(TestBed.inject(HttpTestingController) as any);

      expect(newService.isAuthenticated()).toBe(true);
      expect(newService.currentUser()).toEqual(mockUser);
    });

    it('should handle invalid JSON in localStorage', () => {
      localStorage.setItem('currentUser', 'invalid-json');

      const consoleErrorSpy = spyOn(console, 'error');

      const newService = new AuthService(TestBed.inject(HttpTestingController) as any);

      expect(newService.isAuthenticated()).toBe(false);
      expect(localStorage.getItem('currentUser')).toBeNull();
      expect(consoleErrorSpy).toHaveBeenCalled();
    });

    it('should clear "undefined" string from localStorage', () => {
      localStorage.setItem('currentUser', 'undefined');

      const newService = new AuthService(TestBed.inject(HttpTestingController) as any);

      expect(newService.isAuthenticated()).toBe(false);
      expect(localStorage.getItem('currentUser')).toBeNull();
    });

    it('should clear "null" string from localStorage', () => {
      localStorage.setItem('currentUser', 'null');

      const newService = new AuthService(TestBed.inject(HttpTestingController) as any);

      expect(newService.isAuthenticated()).toBe(false);
      expect(localStorage.getItem('currentUser')).toBeNull();
    });

    it('should handle empty localStorage', () => {
      localStorage.clear();

      const newService = new AuthService(TestBed.inject(HttpTestingController) as any);

      expect(newService.isAuthenticated()).toBe(false);
      expect(newService.currentUser()).toBeNull();
    });
  });

  describe('setCurrentUser', () => {
    it('should set user and save to localStorage', () => {
      localStorage.setItem('auth_token', 'mock-jwt-token');
      service['setCurrentUser'](mockUser);

      expect(service.currentUser()).toEqual(mockUser);
      expect(service.isAuthenticated()).toBe(true);

      const storedUser = localStorage.getItem('currentUser');
      expect(storedUser).toBeTruthy();
      expect(JSON.parse(storedUser!)).toEqual(mockUser);
    });
  });

  describe('currentUser signal', () => {
    it('should be readonly', () => {
      const currentUserSignal = service.currentUser;
      expect(typeof currentUserSignal).toBe('function');
      expect(currentUserSignal()).toBeNull();
    });

    it('should update when user logs in', (done) => {
      const credentials: LoginRequest = {
        email: 'test@test.com',
        password: 'password123'
      };

      const mockResponse: LoginResponse = {
        id: mockUser.id,
        fullName: mockUser.fullName,
        email: mockUser.email,
        enabled: mockUser.enabled,
        roles: mockUser.roles,
        createdAt: mockUser.createdAt,
        token: 'mock-jwt-token',
        tokenType: 'Bearer',
        message: 'Success'
      };

      expect(service.currentUser()).toBeNull();

      service.login(credentials).subscribe({
        next: () => {
          expect(service.currentUser()).toEqual(mockUser);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/login`);
      req.flush(mockResponse);
    });

    it('should update when user logs out', () => {
      service['currentUserSignal'].set(mockUser);
      expect(service.currentUser()).toEqual(mockUser);

      service.logout();

      expect(service.currentUser()).toBeNull();
    });
  });
});
