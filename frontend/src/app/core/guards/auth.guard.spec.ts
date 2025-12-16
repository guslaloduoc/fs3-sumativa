import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { authGuard, roleGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('Auth Guards', () => {
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let mockRoute: ActivatedRouteSnapshot;
  let mockState: RouterStateSnapshot;

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated', 'hasRole']);
    const routerSpyObj = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpyObj }
      ]
    });

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    // Mock route and state
    mockRoute = {} as ActivatedRouteSnapshot;
    mockState = { url: '/dashboard' } as RouterStateSnapshot;
  });

  describe('authGuard', () => {
    it('should allow access when user is authenticated', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);

      const result = TestBed.runInInjectionContext(() =>
        authGuard(mockRoute, mockState)
      );

      expect(result).toBe(true);
      expect(routerSpy.navigate).not.toHaveBeenCalled();
    });

    it('should deny access and redirect to login when user is not authenticated', () => {
      authServiceSpy.isAuthenticated.and.returnValue(false);

      const result = TestBed.runInInjectionContext(() =>
        authGuard(mockRoute, mockState)
      );

      expect(result).toBe(false);
      expect(routerSpy.navigate).toHaveBeenCalledWith(
        ['/auth/login'],
        { queryParams: { returnUrl: '/dashboard' } }
      );
    });

    it('should preserve returnUrl in query params', () => {
      authServiceSpy.isAuthenticated.and.returnValue(false);
      const customState = { url: '/profile' } as RouterStateSnapshot;

      TestBed.runInInjectionContext(() =>
        authGuard(mockRoute, customState)
      );

      expect(routerSpy.navigate).toHaveBeenCalledWith(
        ['/auth/login'],
        { queryParams: { returnUrl: '/profile' } }
      );
    });

    it('should work with different URLs', () => {
      const testCases = [
        '/users',
        '/laboratories',
        '/results',
        '/settings'
      ];

      authServiceSpy.isAuthenticated.and.returnValue(false);

      testCases.forEach(url => {
        routerSpy.navigate.calls.reset();
        const state = { url } as RouterStateSnapshot;

        TestBed.runInInjectionContext(() =>
          authGuard(mockRoute, state)
        );

        expect(routerSpy.navigate).toHaveBeenCalledWith(
          ['/auth/login'],
          { queryParams: { returnUrl: url } }
        );
      });
    });

    it('should call isAuthenticated once', () => {
      authServiceSpy.isAuthenticated.and.returnValue(true);

      TestBed.runInInjectionContext(() =>
        authGuard(mockRoute, mockState)
      );

      expect(authServiceSpy.isAuthenticated).toHaveBeenCalledTimes(1);
    });
  });

  describe('roleGuard', () => {
    describe('ADMIN role', () => {
      it('should allow access when user is authenticated and has ADMIN role', () => {
        authServiceSpy.isAuthenticated.and.returnValue(true);
        authServiceSpy.hasRole.and.returnValue(true);

        const adminGuard = roleGuard('ADMIN');
        const result = TestBed.runInInjectionContext(() =>
          adminGuard(mockRoute, mockState)
        );

        expect(result).toBe(true);
        expect(authServiceSpy.hasRole).toHaveBeenCalledWith('ADMIN');
        expect(routerSpy.navigate).not.toHaveBeenCalled();
      });

      it('should deny access and redirect to dashboard when user lacks ADMIN role', () => {
        authServiceSpy.isAuthenticated.and.returnValue(true);
        authServiceSpy.hasRole.and.returnValue(false);

        const adminGuard = roleGuard('ADMIN');
        const result = TestBed.runInInjectionContext(() =>
          adminGuard(mockRoute, mockState)
        );

        expect(result).toBe(false);
        expect(authServiceSpy.hasRole).toHaveBeenCalledWith('ADMIN');
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/dashboard']);
      });

      it('should deny access and redirect to login when user is not authenticated', () => {
        authServiceSpy.isAuthenticated.and.returnValue(false);

        const adminGuard = roleGuard('ADMIN');
        const result = TestBed.runInInjectionContext(() =>
          adminGuard(mockRoute, mockState)
        );

        expect(result).toBe(false);
        expect(authServiceSpy.hasRole).not.toHaveBeenCalled();
        expect(routerSpy.navigate).toHaveBeenCalledWith(
          ['/auth/login'],
          { queryParams: { returnUrl: '/dashboard' } }
        );
      });
    });

    describe('LAB_TECH role', () => {
      it('should allow access when user has LAB_TECH role', () => {
        authServiceSpy.isAuthenticated.and.returnValue(true);
        authServiceSpy.hasRole.and.returnValue(true);

        const labTechGuard = roleGuard('LAB_TECH');
        const result = TestBed.runInInjectionContext(() =>
          labTechGuard(mockRoute, mockState)
        );

        expect(result).toBe(true);
        expect(authServiceSpy.hasRole).toHaveBeenCalledWith('LAB_TECH');
      });

      it('should deny access when user lacks LAB_TECH role', () => {
        authServiceSpy.isAuthenticated.and.returnValue(true);
        authServiceSpy.hasRole.and.returnValue(false);

        const labTechGuard = roleGuard('LAB_TECH');
        const result = TestBed.runInInjectionContext(() =>
          labTechGuard(mockRoute, mockState)
        );

        expect(result).toBe(false);
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/dashboard']);
      });
    });

    describe('DOCTOR role', () => {
      it('should allow access when user has DOCTOR role', () => {
        authServiceSpy.isAuthenticated.and.returnValue(true);
        authServiceSpy.hasRole.and.returnValue(true);

        const doctorGuard = roleGuard('DOCTOR');
        const result = TestBed.runInInjectionContext(() =>
          doctorGuard(mockRoute, mockState)
        );

        expect(result).toBe(true);
        expect(authServiceSpy.hasRole).toHaveBeenCalledWith('DOCTOR');
      });

      it('should deny access when user lacks DOCTOR role', () => {
        authServiceSpy.isAuthenticated.and.returnValue(true);
        authServiceSpy.hasRole.and.returnValue(false);

        const doctorGuard = roleGuard('DOCTOR');
        const result = TestBed.runInInjectionContext(() =>
          doctorGuard(mockRoute, mockState)
        );

        expect(result).toBe(false);
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/dashboard']);
      });
    });

    describe('Multiple roleGuard instances', () => {
      it('should create independent guard functions for different roles', () => {
        authServiceSpy.isAuthenticated.and.returnValue(true);

        const adminGuard = roleGuard('ADMIN');
        const doctorGuard = roleGuard('DOCTOR');

        // Test ADMIN guard
        authServiceSpy.hasRole.and.returnValue(true);
        const adminResult = TestBed.runInInjectionContext(() =>
          adminGuard(mockRoute, mockState)
        );
        expect(adminResult).toBe(true);
        expect(authServiceSpy.hasRole).toHaveBeenCalledWith('ADMIN');

        // Reset spy
        authServiceSpy.hasRole.calls.reset();

        // Test DOCTOR guard
        authServiceSpy.hasRole.and.returnValue(true);
        const doctorResult = TestBed.runInInjectionContext(() =>
          doctorGuard(mockRoute, mockState)
        );
        expect(doctorResult).toBe(true);
        expect(authServiceSpy.hasRole).toHaveBeenCalledWith('DOCTOR');
      });
    });

    describe('Edge cases', () => {
      it('should handle empty role name', () => {
        authServiceSpy.isAuthenticated.and.returnValue(true);
        authServiceSpy.hasRole.and.returnValue(false);

        const emptyRoleGuard = roleGuard('');
        const result = TestBed.runInInjectionContext(() =>
          emptyRoleGuard(mockRoute, mockState)
        );

        expect(result).toBe(false);
        expect(authServiceSpy.hasRole).toHaveBeenCalledWith('');
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/dashboard']);
      });

      it('should handle special characters in role name', () => {
        authServiceSpy.isAuthenticated.and.returnValue(true);
        authServiceSpy.hasRole.and.returnValue(true);

        const specialRoleGuard = roleGuard('SUPER_ADMIN');
        const result = TestBed.runInInjectionContext(() =>
          specialRoleGuard(mockRoute, mockState)
        );

        expect(result).toBe(true);
        expect(authServiceSpy.hasRole).toHaveBeenCalledWith('SUPER_ADMIN');
      });
    });

    describe('returnUrl preservation', () => {
      it('should preserve returnUrl when redirecting to login from role check', () => {
        authServiceSpy.isAuthenticated.and.returnValue(false);

        const customState = { url: '/users' } as RouterStateSnapshot;
        const adminGuard = roleGuard('ADMIN');

        TestBed.runInInjectionContext(() =>
          adminGuard(mockRoute, customState)
        );

        expect(routerSpy.navigate).toHaveBeenCalledWith(
          ['/auth/login'],
          { queryParams: { returnUrl: '/users' } }
        );
      });
    });

    describe('Authentication check sequence', () => {
      it('should check authentication before checking role', () => {
        authServiceSpy.isAuthenticated.and.returnValue(false);

        const adminGuard = roleGuard('ADMIN');

        TestBed.runInInjectionContext(() =>
          adminGuard(mockRoute, mockState)
        );

        expect(authServiceSpy.isAuthenticated).toHaveBeenCalled();
        expect(authServiceSpy.hasRole).not.toHaveBeenCalled();
      });

      it('should check role only after confirming authentication', () => {
        authServiceSpy.isAuthenticated.and.returnValue(true);
        authServiceSpy.hasRole.and.returnValue(true);

        const adminGuard = roleGuard('ADMIN');

        TestBed.runInInjectionContext(() =>
          adminGuard(mockRoute, mockState)
        );

        expect(authServiceSpy.isAuthenticated).toHaveBeenCalled();
        expect(authServiceSpy.hasRole).toHaveBeenCalled();
      });
    });
  });

  describe('Guard composition', () => {
    it('should work together - auth then role', () => {
      // First check with authGuard
      authServiceSpy.isAuthenticated.and.returnValue(true);
      const authResult = TestBed.runInInjectionContext(() =>
        authGuard(mockRoute, mockState)
      );
      expect(authResult).toBe(true);

      // Then check with roleGuard
      authServiceSpy.hasRole.and.returnValue(true);
      const adminGuard = roleGuard('ADMIN');
      const roleResult = TestBed.runInInjectionContext(() =>
        adminGuard(mockRoute, mockState)
      );
      expect(roleResult).toBe(true);
    });

    it('should block at auth guard if not authenticated', () => {
      authServiceSpy.isAuthenticated.and.returnValue(false);

      const authResult = TestBed.runInInjectionContext(() =>
        authGuard(mockRoute, mockState)
      );

      expect(authResult).toBe(false);
      expect(routerSpy.navigate).toHaveBeenCalledWith(
        ['/auth/login'],
        { queryParams: { returnUrl: '/dashboard' } }
      );
    });
  });
});
