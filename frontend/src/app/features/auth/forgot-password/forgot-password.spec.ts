import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { ForgotPassword } from './forgot-password';

describe('ForgotPassword', () => {
  let component: ForgotPassword;
  let fixture: ComponentFixture<ForgotPassword>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ForgotPassword, ReactiveFormsModule],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(ForgotPassword);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form Initialization', () => {
    it('should initialize forgot password form with empty email field', () => {
      expect(component.forgotPasswordForm).toBeDefined();
      expect(component.forgotPasswordForm.get('email')?.value).toBe('');
    });

    it('should have email control', () => {
      expect(component.forgotPasswordForm.contains('email')).toBe(true);
    });

    it('should initialize with isLoading as false', () => {
      expect(component.isLoading).toBe(false);
    });

    it('should initialize with empty errorMessage', () => {
      expect(component.errorMessage).toBe('');
    });

    it('should initialize with empty successMessage', () => {
      expect(component.successMessage).toBe('');
    });

    it('should initialize with emailSent as false', () => {
      expect(component.emailSent).toBe(false);
    });
  });

  describe('Form Validation', () => {
    it('should invalidate form when email is empty', () => {
      const emailControl = component.forgotPasswordForm.get('email');
      emailControl?.setValue('');
      expect(emailControl?.hasError('required')).toBe(true);
      expect(component.forgotPasswordForm.invalid).toBe(true);
    });

    it('should invalidate form when email format is invalid', () => {
      const emailControl = component.forgotPasswordForm.get('email');

      emailControl?.setValue('invalid-email');
      expect(emailControl?.hasError('email')).toBe(true);

      emailControl?.setValue('test@');
      expect(emailControl?.hasError('email')).toBe(true);

      emailControl?.setValue('@example.com');
      expect(emailControl?.hasError('email')).toBe(true);

      emailControl?.setValue('test..email@example.com');
      expect(emailControl?.hasError('email')).toBe(true);
    });

    it('should validate form when email format is correct', () => {
      const emailControl = component.forgotPasswordForm.get('email');

      emailControl?.setValue('test@example.com');
      expect(emailControl?.hasError('email')).toBe(false);

      emailControl?.setValue('user.name@example.com');
      expect(emailControl?.hasError('email')).toBe(false);

      emailControl?.setValue('user+tag@example.co.uk');
      expect(emailControl?.hasError('email')).toBe(false);
    });

    it('should be valid when email is valid', () => {
      component.forgotPasswordForm.setValue({
        email: 'test@example.com'
      });

      expect(component.forgotPasswordForm.valid).toBe(true);
    });
  });

  describe('Form Getters', () => {
    it('should return email control', () => {
      const emailControl = component.email;
      expect(emailControl).toBe(component.forgotPasswordForm.get('email'));
    });

    it('should return correct value from email getter', () => {
      component.forgotPasswordForm.get('email')?.setValue('test@example.com');
      expect(component.email?.value).toBe('test@example.com');
    });
  });

  describe('onSubmit', () => {
    it('should not submit when form is invalid', () => {
      component.forgotPasswordForm.setValue({
        email: ''
      });

      component.onSubmit();

      expect(component.isLoading).toBe(false);
      expect(component.emailSent).toBe(false);
    });

    it('should mark email field as touched when form is invalid', () => {
      component.forgotPasswordForm.setValue({
        email: ''
      });

      component.onSubmit();

      expect(component.forgotPasswordForm.get('email')?.touched).toBe(true);
    });

    it('should not submit with invalid email format', () => {
      component.forgotPasswordForm.setValue({
        email: 'invalid-email'
      });

      component.onSubmit();

      expect(component.emailSent).toBe(false);
    });

    it('should send reset email successfully with valid email', fakeAsync(() => {
      component.forgotPasswordForm.setValue({
        email: 'test@example.com'
      });

      component.onSubmit();

      expect(component.isLoading).toBe(true);
      expect(component.errorMessage).toBe('');
      expect(component.successMessage).toBe('');

      tick(2000);

      expect(component.isLoading).toBe(false);
      expect(component.emailSent).toBe(true);
      expect(component.successMessage).toBe('Se han enviado las instrucciones a tu correo electrónico.');
    }));

    it('should set isLoading to true immediately on submit', () => {
      component.forgotPasswordForm.setValue({
        email: 'test@example.com'
      });

      component.onSubmit();

      expect(component.isLoading).toBe(true);
    });

    it('should clear error message on new submission', fakeAsync(() => {
      component.errorMessage = 'Previous error';

      component.forgotPasswordForm.setValue({
        email: 'test@example.com'
      });

      component.onSubmit();

      expect(component.errorMessage).toBe('');

      tick(2000);
    }));

    it('should clear success message on new submission', fakeAsync(() => {
      component.successMessage = 'Previous success';

      component.forgotPasswordForm.setValue({
        email: 'test@example.com'
      });

      component.onSubmit();

      expect(component.successMessage).toBe('');

      tick(2000);

      expect(component.successMessage).toBe('Se han enviado las instrucciones a tu correo electrónico.');
    }));

    it('should handle multiple email formats correctly', fakeAsync(() => {
      const emails = [
        'user@example.com',
        'test.user@domain.com',
        'admin+tag@company.co.uk'
      ];

      emails.forEach(email => {
        component.forgotPasswordForm.setValue({ email });
        component.onSubmit();

        tick(2000);

        expect(component.emailSent).toBe(true);
        expect(component.successMessage).toBe('Se han enviado las instrucciones a tu correo electrónico.');

        // Reset for next iteration
        component.emailSent = false;
        component.successMessage = '';
      });
    }));
  });

  describe('Loading State', () => {
    it('should show loading state during email submission', fakeAsync(() => {
      component.forgotPasswordForm.setValue({
        email: 'test@example.com'
      });

      expect(component.isLoading).toBe(false);

      component.onSubmit();

      expect(component.isLoading).toBe(true);

      tick(2000);

      expect(component.isLoading).toBe(false);
    }));

    it('should reset loading state after email is sent', fakeAsync(() => {
      component.forgotPasswordForm.setValue({
        email: 'test@example.com'
      });

      component.onSubmit();

      tick(2000);

      expect(component.isLoading).toBe(false);
    }));
  });

  describe('Email Sent State', () => {
    it('should set emailSent to true after successful submission', fakeAsync(() => {
      component.forgotPasswordForm.setValue({
        email: 'test@example.com'
      });

      expect(component.emailSent).toBe(false);

      component.onSubmit();
      tick(2000);

      expect(component.emailSent).toBe(true);
    }));

    it('should not set emailSent to true if form is invalid', () => {
      component.forgotPasswordForm.setValue({
        email: ''
      });

      component.onSubmit();

      expect(component.emailSent).toBe(false);
    });
  });

  describe('Success Message', () => {
    it('should display success message after email is sent', fakeAsync(() => {
      component.forgotPasswordForm.setValue({
        email: 'test@example.com'
      });

      component.onSubmit();
      tick(2000);

      expect(component.successMessage).toBe('Se han enviado las instrucciones a tu correo electrónico.');
    }));

    it('should clear success message on new submission', fakeAsync(() => {
      // First submission
      component.forgotPasswordForm.setValue({
        email: 'first@example.com'
      });
      component.onSubmit();
      tick(2000);

      expect(component.successMessage).toBe('Se han enviado las instrucciones a tu correo electrónico.');

      // Second submission
      component.forgotPasswordForm.setValue({
        email: 'second@example.com'
      });
      component.onSubmit();

      expect(component.successMessage).toBe('');

      tick(2000);

      expect(component.successMessage).toBe('Se han enviado las instrucciones a tu correo electrónico.');
    }));
  });

  describe('Form Reset Scenarios', () => {
    it('should allow re-submission after first email is sent', fakeAsync(() => {
      // First submission
      component.forgotPasswordForm.setValue({
        email: 'first@example.com'
      });
      component.onSubmit();
      tick(2000);

      expect(component.emailSent).toBe(true);

      // Reset state
      component.emailSent = false;
      component.successMessage = '';

      // Second submission
      component.forgotPasswordForm.setValue({
        email: 'second@example.com'
      });
      component.onSubmit();
      tick(2000);

      expect(component.emailSent).toBe(true);
      expect(component.successMessage).toBe('Se han enviado las instrucciones a tu correo electrónico.');
    }));
  });

  describe('Edge Cases', () => {
    it('should invalidate email with whitespace', () => {
      component.forgotPasswordForm.setValue({
        email: '  test@example.com  '
      });

      // Email with leading/trailing whitespace should be invalid
      expect(component.forgotPasswordForm.valid).toBe(false);
      expect(component.email?.hasError('email')).toBe(true);
    });

    it('should not submit when form is pristine and untouched', () => {
      expect(component.forgotPasswordForm.pristine).toBe(true);
      expect(component.forgotPasswordForm.untouched).toBe(true);

      component.onSubmit();

      expect(component.emailSent).toBe(false);
    });
  });
});
