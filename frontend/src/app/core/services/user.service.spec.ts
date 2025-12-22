import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { UserService } from './user.service';
import { User, CreateUserRequest, UpdateUserRequest } from '../models/user.model';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  const API_URL = 'http://localhost:8080/api/users';

  const mockUser: User = {
    id: 1,
    fullName: 'John Doe',
    email: 'john@example.com',
    enabled: true,
    roles: [{ id: 1, name: 'ADMIN' }],
    createdAt: '2024-01-01T00:00:00'
  };

  const mockUsers: User[] = [
    mockUser,
    {
      id: 2,
      fullName: 'Jane Smith',
      email: 'jane@example.com',
      enabled: true,
      roles: [{ id: 2, name: 'LAB_TECH' }],
      createdAt: '2024-01-02T00:00:00'
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAll', () => {
    it('should return all users', (done) => {
      service.getAll().subscribe({
        next: (users) => {
          expect(users).toEqual(mockUsers);
          expect(users.length).toBe(2);
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      expect(req.request.method).toBe('GET');
      req.flush(mockUsers);
    });

    it('should return empty array when no users exist', (done) => {
      service.getAll().subscribe({
        next: (users) => {
          expect(users).toEqual([]);
          expect(users.length).toBe(0);
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush([]);
    });

    it('should handle error response', (done) => {
      service.getAll().subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush('Error fetching users', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('getById', () => {
    it('should return a user by id', (done) => {
      const userId = 1;

      service.getById(userId).subscribe({
        next: (user) => {
          expect(user).toEqual(mockUser);
          expect(user.id).toBe(userId);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockUser);
    });

    it('should handle user not found', (done) => {
      const userId = 999;

      service.getById(userId).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}`);
      req.flush('User not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('getByEmail', () => {
    it('should return a user by email', (done) => {
      const email = 'john@example.com';

      service.getByEmail(email).subscribe({
        next: (user) => {
          expect(user).toEqual(mockUser);
          expect(user.email).toBe(email);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/email/${email}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockUser);
    });

    it('should handle user not found by email', (done) => {
      const email = 'nonexistent@example.com';

      service.getByEmail(email).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/email/${email}`);
      req.flush('User not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('create', () => {
    it('should create a new user', (done) => {
      const newUser: CreateUserRequest = {
        fullName: 'New User',
        email: 'new@example.com',
        password: 'password123',
        enabled: true
      };

      const createdUser: User = {
        id: 3,
        fullName: newUser.fullName,
        email: newUser.email,
        enabled: newUser.enabled!,
        roles: [],
        createdAt: '2024-01-03T00:00:00'
      };

      service.create(newUser).subscribe({
        next: (user) => {
          expect(user).toEqual(createdUser);
          expect(user.fullName).toBe(newUser.fullName);
          expect(user.email).toBe(newUser.email);
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newUser);
      req.flush(createdUser);
    });

    it('should handle validation errors', (done) => {
      const invalidUser: CreateUserRequest = {
        fullName: '',
        email: 'invalid-email',
        password: '123'
      };

      service.create(invalidUser).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush('Validation failed', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('update', () => {
    it('should update an existing user', (done) => {
      const userId = 1;
      const updateData: UpdateUserRequest = {
        fullName: 'John Doe Updated',
        email: 'john.updated@example.com'
      };

      const updatedUser: User = {
        ...mockUser,
        ...updateData
      };

      service.update(userId, updateData).subscribe({
        next: (user) => {
          expect(user).toEqual(updatedUser);
          expect(user.fullName).toBe(updateData.fullName!);
          expect(user.email).toBe(updateData.email!);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateData);
      req.flush(updatedUser);
    });

    it('should handle partial updates', (done) => {
      const userId = 1;
      const updateData: UpdateUserRequest = {
        fullName: 'Only Name Changed'
      };

      const updatedUser: User = {
        ...mockUser,
        fullName: updateData.fullName!
      };

      service.update(userId, updateData).subscribe({
        next: (user) => {
          expect(user.fullName).toBe(updateData.fullName!);
          expect(user.email).toBe(mockUser.email);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}`);
      req.flush(updatedUser);
    });

    it('should handle user not found for update', (done) => {
      const userId = 999;
      const updateData: UpdateUserRequest = {
        fullName: 'New Name'
      };

      service.update(userId, updateData).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}`);
      req.flush('User not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('delete', () => {
    it('should delete a user', (done) => {
      const userId = 1;

      service.delete(userId).subscribe({
        next: () => {
          expect(true).toBe(true);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle user not found for deletion', (done) => {
      const userId = 999;

      service.delete(userId).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}`);
      req.flush('User not found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle deletion of protected user', (done) => {
      const userId = 1;

      service.delete(userId).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}`);
      req.flush('Cannot delete ADMIN user', { status: 403, statusText: 'Forbidden' });
    });
  });

  describe('toggleEnabled', () => {
    it('should toggle user enabled status', (done) => {
      const userId = 1;
      const toggledUser: User = {
        ...mockUser,
        enabled: false
      };

      service.toggleEnabled(userId).subscribe({
        next: (user) => {
          expect(user).toEqual(toggledUser);
          expect(user.enabled).toBe(false);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}/toggle-enabled`);
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual({});
      req.flush(toggledUser);
    });

    it('should toggle from disabled to enabled', (done) => {
      const userId = 2;
      const disabledUser: User = {
        ...mockUser,
        id: userId,
        enabled: false
      };
      const enabledUser: User = {
        ...disabledUser,
        enabled: true
      };

      service.toggleEnabled(userId).subscribe({
        next: (user) => {
          expect(user.enabled).toBe(true);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}/toggle-enabled`);
      req.flush(enabledUser);
    });

    it('should handle error when toggling protected user', (done) => {
      const userId = 1;

      service.toggleEnabled(userId).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}/toggle-enabled`);
      req.flush('Cannot disable ADMIN user', { status: 403, statusText: 'Forbidden' });
    });
  });

  describe('assignRole', () => {
    it('should assign a role to user', (done) => {
      const userId = 1;
      const roleName = 'LAB_TECH';
      const userWithNewRole: User = {
        ...mockUser,
        roles: [
          ...mockUser.roles,
          { id: 2, name: roleName }
        ]
      };

      service.assignRole(userId, roleName).subscribe({
        next: (user) => {
          expect(user.roles.length).toBe(2);
          expect(user.roles.some(r => r.name === roleName)).toBe(true);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}/roles/${roleName}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({});
      req.flush(userWithNewRole);
    });

    it('should handle assigning duplicate role', (done) => {
      const userId = 1;
      const roleName = 'ADMIN';

      service.assignRole(userId, roleName).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}/roles/${roleName}`);
      req.flush('User already has role', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle invalid role name', (done) => {
      const userId = 1;
      const roleName = 'INVALID_ROLE';

      service.assignRole(userId, roleName).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}/roles/${roleName}`);
      req.flush('Role not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('removeRole', () => {
    it('should remove a role from user', (done) => {
      const userId = 1;
      const roleName = 'ADMIN';
      const userWithoutRole: User = {
        ...mockUser,
        roles: []
      };

      service.removeRole(userId, roleName).subscribe({
        next: (user) => {
          expect(user.roles.length).toBe(0);
          expect(user.roles.some(r => r.name === roleName)).toBe(false);
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}/roles/${roleName}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(userWithoutRole);
    });

    it('should handle removing non-existent role', (done) => {
      const userId = 1;
      const roleName = 'LAB_TECH';

      service.removeRole(userId, roleName).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}/roles/${roleName}`);
      req.flush('User does not have role', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle removing last role', (done) => {
      const userId = 1;
      const roleName = 'ADMIN';

      service.removeRole(userId, roleName).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/${userId}/roles/${roleName}`);
      req.flush('User must have at least one role', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('API_URL configuration', () => {
    it('should use correct API endpoint', () => {
      expect(service['API_URL']).toBe('http://localhost:8080/api/users');
    });
  });
});
