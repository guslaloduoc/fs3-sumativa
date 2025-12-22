package com.sumativa.ms_laboratorios.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private String testSecret = "miClaveSecretaMuyLargaParaJWT256bits12345678901234567890";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 86400000L);
    }

    private String generateTestToken(String email, List<String> roles) {
        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }

    private String generateExpiredToken(String email, List<String> roles) {
        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(new Date(System.currentTimeMillis() - 200000))
                .expiration(new Date(System.currentTimeMillis() - 100000))
                .signWith(key)
                .compact();
    }

    @Test
    void validateToken_shouldReturnTrue_forValidToken() {
        String token = generateTestToken("test@example.com", Arrays.asList("ADMIN"));

        boolean result = jwtTokenProvider.validateToken(token);

        assertTrue(result);
    }

    @Test
    void validateToken_shouldReturnFalse_forExpiredToken() {
        String token = generateExpiredToken("test@example.com", Arrays.asList("ADMIN"));

        boolean result = jwtTokenProvider.validateToken(token);

        assertFalse(result);
    }

    @Test
    void validateToken_shouldReturnFalse_forMalformedToken() {
        String malformedToken = "this.is.not.a.valid.token";

        boolean result = jwtTokenProvider.validateToken(malformedToken);

        assertFalse(result);
    }

    @Test
    void validateToken_shouldReturnFalse_forEmptyToken() {
        boolean result = jwtTokenProvider.validateToken("");

        assertFalse(result);
    }

    @Test
    void getEmailFromToken_shouldReturnEmail() {
        String token = generateTestToken("admin@example.com", Arrays.asList("ADMIN"));

        String email = jwtTokenProvider.getEmailFromToken(token);

        assertEquals("admin@example.com", email);
    }

    @Test
    void getRolesFromToken_shouldReturnRoles() {
        List<String> expectedRoles = Arrays.asList("ADMIN", "DOCTOR");
        String token = generateTestToken("test@example.com", expectedRoles);

        List<String> roles = jwtTokenProvider.getRolesFromToken(token);

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("DOCTOR"));
    }

    @Test
    void getRolesFromToken_shouldReturnEmptyList_whenNoRoles() {
        String token = generateTestToken("test@example.com", Arrays.asList());

        List<String> roles = jwtTokenProvider.getRolesFromToken(token);

        assertTrue(roles.isEmpty());
    }

    @Test
    void getRolesFromToken_shouldReturnSingleRole() {
        String token = generateTestToken("test@example.com", Arrays.asList("USER"));

        List<String> roles = jwtTokenProvider.getRolesFromToken(token);

        assertEquals(1, roles.size());
        assertEquals("USER", roles.get(0));
    }

    @Test
    void validateToken_shouldReturnFalse_forNullToken() {
        boolean result = jwtTokenProvider.validateToken(null);

        assertFalse(result);
    }

    @Test
    void validateToken_shouldReturnFalse_forRandomString() {
        boolean result = jwtTokenProvider.validateToken("random_string_not_jwt");

        assertFalse(result);
    }
}
