package com.sumativa.ms_usuarios.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructor_withAllArgs_shouldSetAllFields() {
        Role role = new Role(1L, "ADMIN");
        Set<Role> roles = new HashSet<>(Set.of(role));
        LocalDateTime createdAt = LocalDateTime.now();

        User user = new User(1L, "Test User", "test@example.com", "password", true, createdAt, roles);

        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getFullName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPasswordHash());
        assertTrue(user.getEnabled());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void noArgsConstructor_shouldCreateUserWithDefaults() {
        User user = new User();

        assertNull(user.getId());
        assertNull(user.getFullName());
        assertTrue(user.getEnabled());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        user.setPasswordHash("secret");
        user.setEnabled(false);
        user.setCreatedAt(now);

        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getFullName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("secret", user.getPasswordHash());
        assertFalse(user.getEnabled());
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    void roles_shouldBeModifiable() {
        User user = new User();
        Role role1 = new Role(1L, "ADMIN");
        Role role2 = new Role(2L, "USER");

        user.getRoles().add(role1);
        user.getRoles().add(role2);

        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(role1));
    }

    @Test
    void equals_shouldReturnTrue_forSameFields() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("test@example.com");

        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("test@example.com");

        assertEquals(user1, user2);
    }

    @Test
    void equals_shouldReturnFalse_forDifferentId() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        assertNotEquals(user1, user2);
    }

    @Test
    void equals_shouldReturnTrue_forSameObject() {
        User user = new User();
        user.setId(1L);

        assertEquals(user, user);
    }

    @Test
    void equals_shouldReturnFalse_forNull() {
        User user = new User();
        user.setId(1L);

        assertNotEquals(user, null);
    }

    @Test
    void equals_shouldReturnFalse_forDifferentClass() {
        User user = new User();
        user.setId(1L);

        assertNotEquals(user, "string");
    }

    @Test
    void hashCode_shouldBeConsistent() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        int hash1 = user.hashCode();
        int hash2 = user.hashCode();

        assertEquals(hash1, hash2);
    }

    @Test
    void hashCode_shouldBeEqual_forEqualObjects() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("test@example.com");

        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("test@example.com");

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void toString_shouldNotThrowException() {
        User user = new User();
        user.setId(1L);
        user.setFullName("Test");

        assertDoesNotThrow(() -> user.toString());
        assertNotNull(user.toString());
    }

    @Test
    void enabled_defaultValue_shouldBeTrue() {
        User user = new User();
        assertTrue(user.getEnabled());
    }

    @Test
    void roles_defaultValue_shouldBeEmptySet() {
        User user = new User();
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }
}
