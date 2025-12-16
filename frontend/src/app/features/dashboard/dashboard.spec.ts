import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { Dashboard } from './dashboard';
import { AuthService } from '../../core/services/auth.service';
import { signal } from '@angular/core';

describe('Dashboard', () => {
  let component: Dashboard;
  let fixture: ComponentFixture<Dashboard>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: Router;
  let navigateSpy: jasmine.Spy;

  const mockUser = {
    id: 1,
    fullName: 'Test User',
    email: 'test@example.com',
    enabled: true,
    roles: [{ id: 1, name: 'ADMIN' }],
    createdAt: '2024-01-01T00:00:00'
  };

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated', 'hasRole'], {
      currentUser: signal(mockUser)
    });

    await TestBed.configureTestingModule({
      imports: [Dashboard],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authSpy }
      ]
    }).compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router);
    navigateSpy = spyOn(routerSpy, 'navigate').and.returnValue(Promise.resolve(true));

    fixture = TestBed.createComponent(Dashboard);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Initialization', () => {
    it('should have authService injected', () => {
      expect(component.authService).toBeDefined();
    });

    it('should have router injected', () => {
      expect(component.router).toBeDefined();
    });
  });

  describe('ngOnInit', () => {
    it('should navigate to login when user is not authenticated', () => {
      authServiceSpy.isAuthenticated.and.returnValue(false);
      authServiceSpy.hasRole.and.returnValue(false);

      fixture.detectChanges();

      expect(authServiceSpy.isAuthenticated).toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledWith(['/auth/login']);
    });

    it('should not navigate when user is authenticated', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      expect(authServiceSpy.isAuthenticated).toHaveBeenCalled();
      expect(navigateSpy).not.toHaveBeenCalled();
    });

    it('should call isAuthenticated on initialization', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      expect(authServiceSpy.isAuthenticated).toHaveBeenCalledTimes(1);
    });
  });

  describe('currentUser getter', () => {
    it('should return current user from authService', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      const user = component.currentUser;
      expect(user).toEqual(mockUser);
    });

    it('should reflect currentUser property', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      expect(component.currentUser).toBeDefined();
      expect(component.currentUser).toEqual(mockUser);
    });
  });

  describe('Authentication Flow', () => {
    it('should redirect unauthenticated users to login page', () => {
      authServiceSpy.isAuthenticated.and.returnValue(false);
      authServiceSpy.hasRole.and.returnValue(false);

      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['/auth/login']);
    });

    it('should allow authenticated users to access dashboard', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      expect(navigateSpy).not.toHaveBeenCalled();
      expect(component.currentUser).toEqual(mockUser);
    });

    it('should check authentication status only once on init', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      expect(authServiceSpy.isAuthenticated).toHaveBeenCalledTimes(1);
    });
  });

  describe('User Data Access', () => {
    it('should access user full name', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      const user = component.currentUser;
      expect(user?.fullName).toBe('Test User');
    });

    it('should access user email', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      const user = component.currentUser;
      expect(user?.email).toBe('test@example.com');
    });

    it('should access user roles', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      const user = component.currentUser;
      expect(user?.roles).toEqual([{ id: 1, name: 'ADMIN' }]);
    });

    it('should access user enabled status', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      const user = component.currentUser;
      expect(user?.enabled).toBe(true);
    });
  });

  describe('Edge Cases', () => {
    it('should handle authenticated user', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      expect(() => component.currentUser).not.toThrow();
      expect(component.currentUser).toBeDefined();
    });

    it('should access user properties safely', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      const user = component.currentUser;
      expect(user?.enabled).toBe(true);
      expect(user?.roles).toBeDefined();
    });
  });

  describe('Component Lifecycle', () => {
    it('should initialize properly when created', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      expect(() => fixture.detectChanges()).not.toThrow();
    });

    it('should clean up properly when destroyed', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);
      authServiceSpy.hasRole.and.returnValue(true);

      fixture.detectChanges();

      expect(() => fixture.destroy()).not.toThrow();
    });
  });
});
