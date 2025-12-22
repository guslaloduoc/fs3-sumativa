package com.sumativa.ms_laboratorios.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldSetAuthentication_whenValidToken() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String email = "test@example.com";
        List<String> roles = Arrays.asList("ADMIN", "USER");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRolesFromToken(token)).thenReturn(roles);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(email, authentication.getPrincipal());
        assertEquals(2, authentication.getAuthorities().size());
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenNoToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenEmptyToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenInvalidToken() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenProvider, never()).getEmailFromToken(anyString());
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenNoBearerPrefix() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldContinueFilterChain_whenExceptionOccurs() throws ServletException, IOException {
        String token = "problematic.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenThrow(new RuntimeException("JWT error"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldSetCorrectAuthorities_withSingleRole() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String email = "admin@example.com";
        List<String> roles = Arrays.asList("ADMIN");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRolesFromToken(token)).thenReturn(roles);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(1, authentication.getAuthorities().size());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void doFilterInternal_shouldHandleEmptyRolesList() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String email = "user@example.com";
        List<String> roles = Arrays.asList();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRolesFromToken(token)).thenReturn(roles);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().isEmpty());
    }

    @Test
    void doFilterInternal_shouldSetAuthenticationDetails() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String email = "test@example.com";
        List<String> roles = Arrays.asList("USER");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRolesFromToken(token)).thenReturn(roles);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertNotNull(authentication.getDetails());
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenOnlyBearerWord() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldHandleMultipleRoles() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String email = "admin@example.com";
        List<String> roles = Arrays.asList("ADMIN", "DOCTOR", "USER");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRolesFromToken(token)).thenReturn(roles);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(3, authentication.getAuthorities().size());
    }
}
