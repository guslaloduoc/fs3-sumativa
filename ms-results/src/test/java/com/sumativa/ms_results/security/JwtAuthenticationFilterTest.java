package com.sumativa.ms_results.security;

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

/**
 * Tests unitarios para JwtAuthenticationFilter
 */
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

    // ==================== doFilterInternal Tests ====================

    @Test
    void doFilterInternal_shouldSetAuthentication_whenValidToken() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String email = "test@example.com";
        List<String> roles = Arrays.asList("ADMIN", "USER");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRolesFromToken(token)).thenReturn(roles);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(email, authentication.getPrincipal());
        assertEquals(2, authentication.getAuthorities().size());
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenNoToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenEmptyToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenInvalidToken() throws ServletException, IOException {
        // Arrange
        String token = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenProvider, never()).getEmailFromToken(anyString());
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenNoBearerPrefix() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldContinueFilterChain_whenExceptionOccurs() throws ServletException, IOException {
        // Arrange
        String token = "problematic.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenThrow(new RuntimeException("JWT parsing error"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldSetCorrectAuthorities_withSingleRole() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String email = "admin@example.com";
        List<String> roles = Arrays.asList("ADMIN");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRolesFromToken(token)).thenReturn(roles);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(1, authentication.getAuthorities().size());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void doFilterInternal_shouldHandleEmptyRolesList() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String email = "user@example.com";
        List<String> roles = Arrays.asList();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRolesFromToken(token)).thenReturn(roles);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().isEmpty());
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenOnlyBearerWord() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        // With just "Bearer ", substring(7) gives empty string, StringUtils.hasText returns false
    }

    @Test
    void doFilterInternal_shouldSetAuthenticationDetails() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String email = "test@example.com";
        List<String> roles = Arrays.asList("USER");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRolesFromToken(token)).thenReturn(roles);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertNotNull(authentication.getDetails());
    }
}
