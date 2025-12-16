import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Register } from './register';
import { UserService } from '../../../core/services/user.service';
import { User } from '../../../core/models/user.model';

describe('Register', () => {
  let component: Register;
  let fixture: ComponentFixture<Register>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let routerSpy: Router;
  let navigateSpy: jasmine.Spy;

  const mockCreatedUser: User = {
    id: 1,
    fullName: 'Test User',
    email: 'test@example.com',
    enabled: true,
    roles: [],
    createdAt: '2024-01-01T00:00:00'
  };

  beforeEach(async () => {
    const userSpy = jasmine.createSpyObj('UserService', ['create']);

    await TestBed.configureTestingModule({
      imports: [Register, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        { provide: UserService, useValue: userSpy }
      ]
    }).compileComponents();

    userServiceSpy = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    routerSpy = TestBed.inject(Router);
    navigateSpy = spyOn(routerSpy, 'navigate').and.returnValue(Promise.resolve(true));

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form Initialization', () => {
    it('should initialize register form with empty fields', () => {
      expect(component.registerForm).toBeDefined();
      expect(component.registerForm.get('fullName')?.value).toBe('');
      expect(component.registerForm.get('email')?.value).toBe('');
      expect(component.registerForm.get('password')?.value).toBe('');
      expect(component.registerForm.get('confirmPassword')?.value).toBe('');
      expect(component.registerForm.get('terms')?.value).toBe(false);
    });

    it('should initialize state variables correctly', () => {
      expect(component.isLoading).toBe(false);
      expect(component.showPassword).toBe(false);
      expect(component.showConfirmPassword).toBe(false);
      expect(component.errorMessage).toBe('');
      expect(component.successMessage).toBe('');
    });
  });

  describe('Form Validation - Full Name', () => {
    it('should require fullName', () => {
      const fullName = component.registerForm.get('fullName');
      fullName?.setValue('');
      expect(fullName?.hasError('required')).toBe(true);
    });

    it('should require minimum 3 characters for fullName', () => {
      const fullName = component.registerForm.get('fullName');
      fullName?.setValue('AB');
      expect(fullName?.hasError('minlength')).toBe(true);

      fullName?.setValue('ABC');
      expect(fullName?.hasError('minlength')).toBe(false);
    });

    it('should require maximum 100 characters for fullName', () => {
      const fullName = component.registerForm.get('fullName');
      const longName = 'A'.repeat(101);

      fullName?.setValue(longName);
      expect(fullName?.hasError('maxlength')).toBe(true);

      fullName?.setValue('A'.repeat(100));
      expect(fullName?.hasError('maxlength')).toBe(false);
    });
  });

  describe('Form Validation - Email', () => {
    it('should require email', () => {
      const email = component.registerForm.get('email');
      email?.setValue('');
      expect(email?.hasError('required')).toBe(true);
    });

    it('should validate email format', () => {
      const email = component.registerForm.get('email');

      email?.setValue('invalid-email');
      expect(email?.hasError('email')).toBe(true);

      email?.setValue('test@example.com');
      expect(email?.hasError('email')).toBe(false);
    });
  });

  describe('Form Validation - Password Strength', () => {
    it('should require password', () => {
      const password = component.registerForm.get('password');
      password?.setValue('');
      expect(password?.hasError('required')).toBe(true);
    });

    it('should require minimum 8 characters', () => {
      const password = component.registerForm.get('password');
      password?.setValue('Abc1@');
      expect(password?.hasError('minlength')).toBe(true);

      password?.setValue('Abc1@678');
      expect(password?.hasError('minlength')).toBe(false);
    });

    it('should require uppercase letter', () => {
      const password = component.registerForm.get('password');
      password?.setValue('abc123!@');
      expect(password?.hasError('passwordStrength')).toBe(true);
    });

    it('should require number', () => {
      const password = component.registerForm.get('password');
      password?.setValue('Abcdefg!');
      expect(password?.hasError('passwordStrength')).toBe(true);
    });

    it('should require special character', () => {
      const password = component.registerForm.get('password');
      password?.setValue('Abcd1234');
      expect(password?.hasError('passwordStrength')).toBe(true);
    });

    it('should accept strong password', () => {
      const password = component.registerForm.get('password');
      password?.setValue('Abc123!@');
      expect(password?.hasError('passwordStrength')).toBe(false);
      expect(password?.hasError('minlength')).toBe(false);
    });
  });

  describe('Password Strength Validator', () => {
    it('should return null for empty value', () => {
      const result = component.passwordStrengthValidator({ value: '' } as any);
      expect(result).toBeNull();
    });

    it('should return error for weak password', () => {
      const result = component.passwordStrengthValidator({ value: 'weakpass' } as any);
      expect(result).toEqual({ passwordStrength: true });
    });

    it('should return null for strong password', () => {
      const result = component.passwordStrengthValidator({ value: 'Strong123!' } as any);
      expect(result).toBeNull();
    });
  });

  describe('Form Validation - Password Match', () => {
    it('should require confirmPassword', () => {
      const confirmPassword = component.registerForm.get('confirmPassword');
      confirmPassword?.setValue('');
      expect(confirmPassword?.hasError('required')).toBe(true);
    });

    it('should show error when passwords do not match', () => {
      component.registerForm.patchValue({
        password: 'Password123!',
        confirmPassword: 'Different123!'
      });

      expect(component.registerForm.hasError('passwordMismatch')).toBe(true);
    });

    it('should not show error when passwords match', () => {
      component.registerForm.patchValue({
        password: 'Password123!',
        confirmPassword: 'Password123!'
      });

      expect(component.registerForm.hasError('passwordMismatch')).toBe(false);
    });
  });

  describe('Password Match Validator', () => {
    it('should return null when controls are missing', () => {
      const mockControl = {
        get: (name: string) => null
      } as any;

      const result = component.passwordMatchValidator(mockControl);
      expect(result).toBeNull();
    });

    it('should return error when passwords mismatch', () => {
      const result = component.passwordMatchValidator(component.registerForm);
      component.registerForm.patchValue({
        password: 'Pass1!',
        confirmPassword: 'Pass2!'
      });

      const validationResult = component.passwordMatchValidator(component.registerForm);
      expect(validationResult).toEqual({ passwordMismatch: true });
    });

    it('should return null when passwords match', () => {
      component.registerForm.patchValue({
        password: 'Password123!',
        confirmPassword: 'Password123!'
      });

      const result = component.passwordMatchValidator(component.registerForm);
      expect(result).toBeNull();
    });
  });

  describe('Form Validation - Terms', () => {
    it('should require terms to be accepted', () => {
      const terms = component.registerForm.get('terms');
      terms?.setValue(false);
      expect(terms?.hasError('required')).toBe(true);

      terms?.setValue(true);
      expect(terms?.hasError('required')).toBe(false);
    });
  });

  describe('Form Getters', () => {
    it('should return correct form controls', () => {
      expect(component.fullName).toBe(component.registerForm.get('fullName'));
      expect(component.email).toBe(component.registerForm.get('email'));
      expect(component.password).toBe(component.registerForm.get('password'));
      expect(component.confirmPassword).toBe(component.registerForm.get('confirmPassword'));
      expect(component.terms).toBe(component.registerForm.get('terms'));
    });
  });

  describe('Password Visibility Toggle', () => {
    it('should toggle password visibility', () => {
      expect(component.showPassword).toBe(false);

      component.togglePasswordVisibility();
      expect(component.showPassword).toBe(true);

      component.togglePasswordVisibility();
      expect(component.showPassword).toBe(false);
    });

    it('should toggle confirm password visibility', () => {
      expect(component.showConfirmPassword).toBe(false);

      component.toggleConfirmPasswordVisibility();
      expect(component.showConfirmPassword).toBe(true);

      component.toggleConfirmPasswordVisibility();
      expect(component.showConfirmPassword).toBe(false);
    });
  });

  describe('onSubmit', () => {
    it('should not submit when form is invalid', () => {
      component.registerForm.setValue({
        fullName: '',
        email: '',
        password: '',
        confirmPassword: '',
        terms: false
      });

      component.onSubmit();

      expect(userServiceSpy.create).not.toHaveBeenCalled();
    });

    it('should mark all fields as touched when form is invalid', () => {
      component.registerForm.setValue({
        fullName: '',
        email: '',
        password: '',
        confirmPassword: '',
        terms: false
      });

      component.onSubmit();

      expect(component.registerForm.get('fullName')?.touched).toBe(true);
      expect(component.registerForm.get('email')?.touched).toBe(true);
      expect(component.registerForm.get('password')?.touched).toBe(true);
      expect(component.registerForm.get('confirmPassword')?.touched).toBe(true);
      expect(component.registerForm.get('terms')?.touched).toBe(true);
    });

    it('should register user successfully', fakeAsync(() => {
      component.registerForm.setValue({
        fullName: 'Test User',
        email: 'test@example.com',
        password: 'Password123!',
        confirmPassword: 'Password123!',
        terms: true
      });

      userServiceSpy.create.and.returnValue(of(mockCreatedUser));

      component.onSubmit();

      expect(userServiceSpy.create).toHaveBeenCalledWith({
        fullName: 'Test User',
        email: 'test@example.com',
        passwordHash: 'Password123!',
        enabled: true
      });

      tick();

      expect(component.isLoading).toBe(false);
      expect(component.successMessage).toBe('Registro exitoso. Redirigiendo al inicio de sesión...');
      expect(component.errorMessage).toBe('');

      tick(2000);

      expect(navigateSpy).toHaveBeenCalledWith(['/auth/login']);
    }));

    it('should handle registration error with custom message', (done) => {
      component.registerForm.setValue({
        fullName: 'Test User',
        email: 'test@example.com',
        password: 'Password123!',
        confirmPassword: 'Password123!',
        terms: true
      });

      const errorResponse = {
        error: {
          error: 'Email already exists'
        }
      };

      userServiceSpy.create.and.returnValue(throwError(() => errorResponse));

      component.onSubmit();

      setTimeout(() => {
        expect(component.isLoading).toBe(false);
        expect(component.errorMessage).toBe('Email already exists');
        expect(component.successMessage).toBe('');
        expect(navigateSpy).not.toHaveBeenCalled();
        done();
      }, 10);
    });

    it('should handle registration error with default message', (done) => {
      component.registerForm.setValue({
        fullName: 'Test User',
        email: 'test@example.com',
        password: 'Password123!',
        confirmPassword: 'Password123!',
        terms: true
      });

      userServiceSpy.create.and.returnValue(throwError(() => ({})));

      component.onSubmit();

      setTimeout(() => {
        expect(component.errorMessage).toBe('Error al registrar usuario. Intenta nuevamente.');
        done();
      }, 10);
    });

    it('should clear messages on new submission', fakeAsync(() => {
      component.errorMessage = 'Previous error';
      component.successMessage = 'Previous success';

      component.registerForm.setValue({
        fullName: 'Test User',
        email: 'test@example.com',
        password: 'Password123!',
        confirmPassword: 'Password123!',
        terms: true
      });

      userServiceSpy.create.and.returnValue(of(mockCreatedUser));

      component.onSubmit();
      tick();

      expect(component.errorMessage).toBe('');
      expect(component.successMessage).toBe('Registro exitoso. Redirigiendo al inicio de sesión...');
    }));
  });

  describe('Complete Registration Flow', () => {
    it('should complete full registration flow', fakeAsync(() => {
      // Fill form with valid data
      component.registerForm.setValue({
        fullName: 'John Doe',
        email: 'john.doe@example.com',
        password: 'SecureP@ss123',
        confirmPassword: 'SecureP@ss123',
        terms: true
      });

      // Mock successful API call
      userServiceSpy.create.and.returnValue(of(mockCreatedUser));

      // Submit form
      component.onSubmit();

      // Wait for observable to complete
      tick();

      // Verify success state
      expect(component.isLoading).toBe(false);
      expect(component.successMessage).toContain('Registro exitoso');
      expect(component.errorMessage).toBe('');

      // Wait for redirect
      tick(2000);

      // Verify navigation
      expect(navigateSpy).toHaveBeenCalledWith(['/auth/login']);
    }));
  });
});
