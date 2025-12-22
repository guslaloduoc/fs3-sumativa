package com.sumativa.ms_usuarios.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private static final String SECRET = "LabControlSecretKeyForJWTTokenGeneration2025MustBeLongEnoughForHS512Algorithm";
    private static final long EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", EXPIRATION);
    }

    private String generateValidToken(String email, List<String> roles) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    private String generateExpiredToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() - 10000);

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(now.getTime() - 20000))
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String email = "test@example.com";
        List<String> roles = Arrays.asList("ADMIN", "USER");

        String token = jwtTokenProvider.generateToken(email, roles);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.contains("."));
    }

    @Test
    void validateToken_shouldReturnTrue_whenTokenIsValid() {
        String token = generateValidToken("test@example.com", Arrays.asList("ADMIN"));

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsMalformed() {
        boolean isValid = jwtTokenProvider.validateToken("malformed.token.here");

        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsExpired() {
        String token = generateExpiredToken("test@example.com");

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsEmpty() {
        boolean isValid = jwtTokenProvider.validateToken("");

        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsNull() {
        boolean isValid = jwtTokenProvider.validateToken(null);

        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsRandomString() {
        boolean isValid = jwtTokenProvider.validateToken("not-a-jwt-token-at-all");

        assertFalse(isValid);
    }

    @Test
    void getEmailFromToken_shouldReturnEmail() {
        String email = "test@example.com";
        String token = generateValidToken(email, Arrays.asList("ADMIN"));

        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void getEmailFromToken_shouldReturnDifferentEmails() {
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        String token1 = generateValidToken(email1, Arrays.asList("USER"));
        String token2 = generateValidToken(email2, Arrays.asList("ADMIN"));

        assertEquals(email1, jwtTokenProvider.getEmailFromToken(token1));
        assertEquals(email2, jwtTokenProvider.getEmailFromToken(token2));
    }

    @Test
    void getRolesFromToken_shouldReturnRoles() {
        List<String> roles = Arrays.asList("ADMIN", "USER");
        String token = generateValidToken("test@example.com", roles);

        List<String> extractedRoles = jwtTokenProvider.getRolesFromToken(token);

        assertNotNull(extractedRoles);
        assertEquals(2, extractedRoles.size());
        assertTrue(extractedRoles.contains("ADMIN"));
        assertTrue(extractedRoles.contains("USER"));
    }

    @Test
    void getRolesFromToken_shouldReturnSingleRole() {
        List<String> roles = Arrays.asList("LAB_TECH");
        String token = generateValidToken("tech@example.com", roles);

        List<String> extractedRoles = jwtTokenProvider.getRolesFromToken(token);

        assertEquals(1, extractedRoles.size());
        assertEquals("LAB_TECH", extractedRoles.get(0));
    }

    @Test
    void getRolesFromToken_shouldReturnEmptyList_whenNoRoles() {
        List<String> roles = Arrays.asList();
        String token = generateValidToken("test@example.com", roles);

        List<String> extractedRoles = jwtTokenProvider.getRolesFromToken(token);

        assertNotNull(extractedRoles);
        assertTrue(extractedRoles.isEmpty());
    }

    @Test
    void generatedToken_shouldBeValidatable() {
        String email = "test@example.com";
        List<String> roles = Arrays.asList("ADMIN");

        String token = jwtTokenProvider.generateToken(email, roles);
        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void generatedToken_shouldContainCorrectClaims() {
        String email = "test@example.com";
        List<String> roles = Arrays.asList("ADMIN", "DOCTOR");

        String token = jwtTokenProvider.generateToken(email, roles);
        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);
        List<String> extractedRoles = jwtTokenProvider.getRolesFromToken(token);

        assertEquals(email, extractedEmail);
        assertEquals(roles.size(), extractedRoles.size());
        assertTrue(extractedRoles.containsAll(roles));
    }
}
