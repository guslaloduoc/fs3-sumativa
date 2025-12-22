package com.sumativa.ms_usuarios.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void constructor_withAllArgs_shouldSetAllFields() {
        Role role = new Role(1L, "ADMIN");

        assertEquals(1L, role.getId());
        assertEquals("ADMIN", role.getName());
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyRole() {
        Role role = new Role();

        assertNull(role.getId());
        assertNull(role.getName());
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        Role role = new Role();

        role.setId(1L);
        role.setName("DOCTOR");

        assertEquals(1L, role.getId());
        assertEquals("DOCTOR", role.getName());
    }

    @Test
    void equals_shouldReturnTrue_forSameFields() {
        Role role1 = new Role(1L, "ADMIN");
        Role role2 = new Role(1L, "ADMIN");

        assertEquals(role1, role2);
    }

    @Test
    void equals_shouldReturnFalse_forDifferentId() {
        Role role1 = new Role(1L, "ADMIN");
        Role role2 = new Role(2L, "ADMIN");

        assertNotEquals(role1, role2);
    }

    @Test
    void equals_shouldReturnFalse_forDifferentName() {
        Role role1 = new Role(1L, "ADMIN");
        Role role2 = new Role(1L, "USER");

        assertNotEquals(role1, role2);
    }

    @Test
    void equals_shouldReturnTrue_forSameObject() {
        Role role = new Role(1L, "ADMIN");
        assertEquals(role, role);
    }

    @Test
    void equals_shouldReturnFalse_forNull() {
        Role role = new Role(1L, "ADMIN");
        assertNotEquals(role, null);
    }

    @Test
    void equals_shouldReturnFalse_forDifferentClass() {
        Role role = new Role(1L, "ADMIN");
        assertNotEquals(role, "string");
    }

    @Test
    void hashCode_shouldBeConsistent() {
        Role role = new Role(1L, "ADMIN");

        int hash1 = role.hashCode();
        int hash2 = role.hashCode();

        assertEquals(hash1, hash2);
    }

    @Test
    void hashCode_shouldBeEqual_forEqualObjects() {
        Role role1 = new Role(1L, "ADMIN");
        Role role2 = new Role(1L, "ADMIN");

        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void toString_shouldNotThrowException() {
        Role role = new Role(1L, "ADMIN");

        assertDoesNotThrow(() -> role.toString());
        assertNotNull(role.toString());
        assertTrue(role.toString().contains("ADMIN"));
    }

    @Test
    void equals_shouldHandleNullId() {
        Role role1 = new Role();
        role1.setName("ADMIN");

        Role role2 = new Role();
        role2.setName("ADMIN");

        assertEquals(role1, role2);
    }

    @Test
    void hashCode_shouldHandleNullFields() {
        Role role = new Role();
        assertDoesNotThrow(() -> role.hashCode());
    }
}
