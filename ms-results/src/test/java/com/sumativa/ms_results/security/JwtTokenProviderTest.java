package com.sumativa.ms_results.security;

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

/**
 * Tests unitarios para JwtTokenProvider
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private static final String SECRET = "LabControlSecretKeyForJWTTokenGeneration2025MustBeLongEnoughForHS512Algorithm";
    private static final long EXPIRATION = 86400000L; // 24 hours

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
        Date expiryDate = new Date(now.getTime() - 10000); // Expired 10 seconds ago

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(now.getTime() - 20000))
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // ==================== validateToken Tests ====================

    @Test
    void validateToken_shouldReturnTrue_whenTokenIsValid() {
        // Arrange
        String token = generateValidToken("test@example.com", Arrays.asList("ADMIN"));

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsMalformed() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken("malformed.token.here");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsExpired() {
        // Arrange
        String token = generateExpiredToken("test@example.com");

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsEmpty() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsNull() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsRandomString() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken("not-a-jwt-token-at-all");

        // Assert
        assertFalse(isValid);
    }

    // ==================== getEmailFromToken Tests ====================

    @Test
    void getEmailFromToken_shouldReturnEmail() {
        // Arrange
        String email = "test@example.com";
        String token = generateValidToken(email, Arrays.asList("ADMIN"));

        // Act
        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

        // Assert
        assertEquals(email, extractedEmail);
    }

    @Test
    void getEmailFromToken_shouldReturnDifferentEmailsCorrectly() {
        // Arrange
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        String token1 = generateValidToken(email1, Arrays.asList("USER"));
        String token2 = generateValidToken(email2, Arrays.asList("ADMIN"));

        // Act & Assert
        assertEquals(email1, jwtTokenProvider.getEmailFromToken(token1));
        assertEquals(email2, jwtTokenProvider.getEmailFromToken(token2));
    }

    // ==================== getRolesFromToken Tests ====================

    @Test
    void getRolesFromToken_shouldReturnRoles() {
        // Arrange
        List<String> roles = Arrays.asList("ADMIN", "USER");
        String token = generateValidToken("test@example.com", roles);

        // Act
        List<String> extractedRoles = jwtTokenProvider.getRolesFromToken(token);

        // Assert
        assertNotNull(extractedRoles);
        assertEquals(2, extractedRoles.size());
        assertTrue(extractedRoles.contains("ADMIN"));
        assertTrue(extractedRoles.contains("USER"));
    }

    @Test
    void getRolesFromToken_shouldReturnSingleRole() {
        // Arrange
        List<String> roles = Arrays.asList("LAB_TECH");
        String token = generateValidToken("tech@example.com", roles);

        // Act
        List<String> extractedRoles = jwtTokenProvider.getRolesFromToken(token);

        // Assert
        assertNotNull(extractedRoles);
        assertEquals(1, extractedRoles.size());
        assertEquals("LAB_TECH", extractedRoles.get(0));
    }

    @Test
    void getRolesFromToken_shouldReturnEmptyList_whenNoRoles() {
        // Arrange
        List<String> roles = Arrays.asList();
        String token = generateValidToken("test@example.com", roles);

        // Act
        List<String> extractedRoles = jwtTokenProvider.getRolesFromToken(token);

        // Assert
        assertNotNull(extractedRoles);
        assertTrue(extractedRoles.isEmpty());
    }
}
