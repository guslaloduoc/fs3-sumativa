package com.sumativa.ms_laboratorios.config;

import com.sumativa.ms_laboratorios.security.JwtAuthenticationFilter;
import com.sumativa.ms_laboratorios.security.JwtTokenProvider;
import com.sumativa.ms_laboratorios.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void securityConfig_shouldCreateInstance() {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider);
        SecurityConfig config = new SecurityConfig(filter);

        assertNotNull(config);
    }

    @Test
    void jwtAuthenticationFilter_shouldBeCreated() {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider);

        assertNotNull(filter);
    }
}
