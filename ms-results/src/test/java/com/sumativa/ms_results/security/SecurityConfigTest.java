package com.sumativa.ms_results.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para SecurityConfig
 */
@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void securityConfig_shouldBeInstantiated() {
        // Assert
        assertNotNull(securityConfig);
    }

    @Test
    void jwtAuthenticationFilter_shouldBeInjected() {
        // Assert
        assertNotNull(jwtAuthenticationFilter);
    }
}
