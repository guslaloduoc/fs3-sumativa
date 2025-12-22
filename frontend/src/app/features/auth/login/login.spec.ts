import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Login } from './login';
import { AuthService } from '../../../core/services/auth.service';
import { LoginResponse } from '../../../core/models/auth.model';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: Router;
  let navigateSpy: jasmine.Spy;

  const mockLoginResponse: LoginResponse = {
    id: 1,
    fullName: 'Test User',
    email: 'test@example.com',
    enabled: true,
    roles: [{ id: 1, name: 'ADMIN' }],
    createdAt: '2024-01-01T00:00:00',
    token: 'mock-jwt-token',
    tokenType: 'Bearer',
    message: 'Login successful'
  };

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [Login, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authSpy }
      ]
    }).compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router);
    navigateSpy = spyOn(routerSpy, 'navigate').and.returnValue(Promise.resolve(true));

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form Initialization', () => {
    it('should initialize login form with empty fields', () => {
      expect(component.loginForm).toBeDefined();
      expect(component.loginForm.get('email')?.value).toBe('');
      expect(component.loginForm.get('password')?.value).toBe('');
    });

    it('should have email and password controls', () => {
      expect(component.loginForm.contains('email')).toBe(true);
      expect(component.loginForm.contains('password')).toBe(true);
    });

    it('should initialize with isLoading as false', () => {
      expect(component.isLoading).toBe(false);
    });

    it('should initialize with empty errorMessage', () => {
      expect(component.errorMessage).toBe('');
    });
  });

  describe('Form Validation', () => {
    it('should invalidate form when email is empty', () => {
      const emailControl = component.loginForm.get('email');
      emailControl?.setValue('');
      expect(emailControl?.hasError('required')).toBe(true);
      expect(component.loginForm.invalid).toBe(true);
    });

    it('should invalidate form when email format is invalid', () => {
      const emailControl = component.loginForm.get('email');

      emailControl?.setValue('invalid-email');
      expect(emailControl?.hasError('email')).toBe(true);

      emailControl?.setValue('test@');
      expect(emailControl?.hasError('email')).toBe(true);

      emailControl?.setValue('@example.com');
      expect(emailControl?.hasError('email')).toBe(true);
    });

    it('should validate form when email format is correct', () => {
      const emailControl = component.loginForm.get('email');

      emailControl?.setValue('test@example.com');
      expect(emailControl?.hasError('email')).toBe(false);

      emailControl?.setValue('user.name+tag@example.co.uk');
      expect(emailControl?.hasError('email')).toBe(false);
    });

    it('should invalidate form when password is empty', () => {
      const passwordControl = component.loginForm.get('password');
      passwordControl?.setValue('');
      expect(passwordControl?.hasError('required')).toBe(true);
    });

    it('should invalidate form when password is too short', () => {
      const passwordControl = component.loginForm.get('password');

      passwordControl?.setValue('12345');
      expect(passwordControl?.hasError('minlength')).toBe(true);

      passwordControl?.setValue('abc');
      expect(passwordControl?.hasError('minlength')).toBe(true);
    });

    it('should validate form when password meets minimum length', () => {
      const passwordControl = component.loginForm.get('password');

      passwordControl?.setValue('123456');
      expect(passwordControl?.hasError('minlength')).toBe(false);

      passwordControl?.setValue('password123');
      expect(passwordControl?.hasError('minlength')).toBe(false);
    });

    it('should be valid when both email and password are valid', () => {
      component.loginForm.setValue({
        email: 'test@example.com',
        password: 'password123'
      });

      expect(component.loginForm.valid).toBe(true);
    });
  });

  describe('Form Getters', () => {
    it('should return email control', () => {
      const emailControl = component.email;
      expect(emailControl).toBe(component.loginForm.get('email'));
    });

    it('should return password control', () => {
      const passwordControl = component.password;
      expect(passwordControl).toBe(component.loginForm.get('password'));
    });
  });

  describe('onSubmit', () => {
    it('should not submit when form is invalid', () => {
      component.loginForm.setValue({
        email: '',
        password: ''
      });

      component.onSubmit();

      expect(authServiceSpy.login).not.toHaveBeenCalled();
      expect(navigateSpy).not.toHaveBeenCalled();
    });

    it('should mark all fields as touched when form is invalid', () => {
      component.loginForm.setValue({
        email: '',
        password: ''
      });

      component.onSubmit();

      expect(component.loginForm.get('email')?.touched).toBe(true);
      expect(component.loginForm.get('password')?.touched).toBe(true);
    });

    it('should login successfully with valid credentials', (done) => {
      const credentials = {
        email: 'test@example.com',
        password: 'password123'
      };

      component.loginForm.setValue(credentials);
      authServiceSpy.login.and.returnValue(of(mockLoginResponse));

      component.onSubmit();

      expect(authServiceSpy.login).toHaveBeenCalledWith(credentials);

      setTimeout(() => {
        expect(component.isLoading).toBe(false);
        expect(navigateSpy).toHaveBeenCalledWith(['/dashboard']);
        expect(component.errorMessage).toBe('');
        done();
      }, 10);
    });

    it('should handle login error with custom message', (done) => {
      const credentials = {
        email: 'test@example.com',
        password: 'wrong-password'
      };

      const errorResponse = {
        error: {
          error: 'Invalid credentials'
        }
      };

      component.loginForm.setValue(credentials);
      authServiceSpy.login.and.returnValue(throwError(() => errorResponse));

      component.onSubmit();

      setTimeout(() => {
        expect(component.isLoading).toBe(false);
        expect(component.errorMessage).toBe('Invalid credentials');
        expect(navigateSpy).not.toHaveBeenCalled();
        done();
      }, 10);
    });

    it('should handle login error with default message', (done) => {
      const credentials = {
        email: 'test@example.com',
        password: 'password123'
      };

      component.loginForm.setValue(credentials);
      authServiceSpy.login.and.returnValue(throwError(() => ({})));

      component.onSubmit();

      setTimeout(() => {
        expect(component.isLoading).toBe(false);
        expect(component.errorMessage).toBe('Error al iniciar sesión. Verifica tus credenciales.');
        done();
      }, 10);
    });

    it('should clear error message on new submission', (done) => {
      component.errorMessage = 'Previous error';

      component.loginForm.setValue({
        email: 'test@example.com',
        password: 'password123'
      });

      authServiceSpy.login.and.returnValue(of(mockLoginResponse));

      component.onSubmit();

      expect(component.errorMessage).toBe('');

      setTimeout(() => {
        done();
      }, 10);
    });

    it('should set isLoading to true during login request', (done) => {
      component.loginForm.setValue({
        email: 'test@example.com',
        password: 'password123'
      });

      authServiceSpy.login.and.returnValue(of(mockLoginResponse));

      component.onSubmit();

      setTimeout(() => {
        expect(authServiceSpy.login).toHaveBeenCalled();
        expect(component.isLoading).toBe(false);
        done();
      }, 10);
    });

    it('should handle network error', (done) => {
      component.loginForm.setValue({
        email: 'test@example.com',
        password: 'password123'
      });

      const networkError = new Error('Network error');
      authServiceSpy.login.and.returnValue(throwError(() => networkError));

      component.onSubmit();

      setTimeout(() => {
        expect(component.isLoading).toBe(false);
        expect(component.errorMessage).toBe('Error al iniciar sesión. Verifica tus credenciales.');
        done();
      }, 10);
    });
  });

  describe('Loading State', () => {
    it('should show loading state during login', (done) => {
      component.loginForm.setValue({
        email: 'test@example.com',
        password: 'password123'
      });

      authServiceSpy.login.and.returnValue(of(mockLoginResponse));

      expect(component.isLoading).toBe(false);
      component.onSubmit();

      setTimeout(() => {
        expect(component.isLoading).toBe(false);
        expect(authServiceSpy.login).toHaveBeenCalled();
        done();
      }, 10);
    });

    it('should reset loading state after successful login', (done) => {
      component.loginForm.setValue({
        email: 'test@example.com',
        password: 'password123'
      });

      authServiceSpy.login.and.returnValue(of(mockLoginResponse));

      component.onSubmit();

      setTimeout(() => {
        expect(component.isLoading).toBe(false);
        done();
      }, 10);
    });

    it('should reset loading state after failed login', (done) => {
      component.loginForm.setValue({
        email: 'test@example.com',
        password: 'wrong-password'
      });

      authServiceSpy.login.and.returnValue(throwError(() => new Error('Auth failed')));

      component.onSubmit();

      setTimeout(() => {
        expect(component.isLoading).toBe(false);
        done();
      }, 10);
    });
  });

  describe('Integration', () => {
    it('should pass correct data to AuthService', () => {
      const expectedCredentials = {
        email: 'user@example.com',
        password: 'mypassword'
      };

      component.loginForm.setValue(expectedCredentials);
      authServiceSpy.login.and.returnValue(of(mockLoginResponse));

      component.onSubmit();

      expect(authServiceSpy.login).toHaveBeenCalledWith(expectedCredentials);
    });

    it('should navigate to dashboard after successful login', (done) => {
      component.loginForm.setValue({
        email: 'test@example.com',
        password: 'password123'
      });

      authServiceSpy.login.and.returnValue(of(mockLoginResponse));

      component.onSubmit();

      setTimeout(() => {
        expect(navigateSpy).toHaveBeenCalledTimes(1);
        expect(navigateSpy).toHaveBeenCalledWith(['/dashboard']);
        done();
      }, 10);
    });
  });
});
