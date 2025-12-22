package com.sumativa.ms_laboratorios.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LaboratorioTest {

    @Test
    void shouldCreateLaboratorioWithNoArgsConstructor() {
        Laboratorio laboratorio = new Laboratorio();

        assertNotNull(laboratorio);
        assertNull(laboratorio.getId());
        assertNull(laboratorio.getNombre());
        assertNull(laboratorio.getDireccion());
        assertNull(laboratorio.getTelefono());
    }

    @Test
    void shouldCreateLaboratorioWithAllArgsConstructor() {
        Laboratorio laboratorio = new Laboratorio(1L, "Lab Test", "Direccion Test", "912345678");

        assertEquals(1L, laboratorio.getId());
        assertEquals("Lab Test", laboratorio.getNombre());
        assertEquals("Direccion Test", laboratorio.getDireccion());
        assertEquals("912345678", laboratorio.getTelefono());
    }

    @Test
    void shouldSetAndGetId() {
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setId(1L);

        assertEquals(1L, laboratorio.getId());
    }

    @Test
    void shouldSetAndGetNombre() {
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setNombre("Lab Test");

        assertEquals("Lab Test", laboratorio.getNombre());
    }

    @Test
    void shouldSetAndGetDireccion() {
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setDireccion("Direccion Test");

        assertEquals("Direccion Test", laboratorio.getDireccion());
    }

    @Test
    void shouldSetAndGetTelefono() {
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setTelefono("912345678");

        assertEquals("912345678", laboratorio.getTelefono());
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        Laboratorio lab1 = new Laboratorio(1L, "Lab Test", "Direccion Test", "912345678");
        Laboratorio lab2 = new Laboratorio(1L, "Lab Test", "Direccion Test", "912345678");
        Laboratorio lab3 = new Laboratorio(2L, "Lab Different", "Otra Direccion", "987654321");

        assertEquals(lab1, lab2);
        assertNotEquals(lab1, lab3);
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        Laboratorio lab1 = new Laboratorio(1L, "Lab Test", "Direccion Test", "912345678");
        Laboratorio lab2 = new Laboratorio(1L, "Lab Test", "Direccion Test", "912345678");

        assertEquals(lab1.hashCode(), lab2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        Laboratorio laboratorio = new Laboratorio(1L, "Lab Test", "Direccion Test", "912345678");

        String toString = laboratorio.toString();

        assertTrue(toString.contains("Lab Test"));
        assertTrue(toString.contains("Direccion Test"));
        assertTrue(toString.contains("912345678"));
    }

    @Test
    void shouldAllowNullDireccion() {
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setId(1L);
        laboratorio.setNombre("Lab Test");
        laboratorio.setDireccion(null);

        assertNull(laboratorio.getDireccion());
    }

    @Test
    void shouldAllowNullTelefono() {
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setId(1L);
        laboratorio.setNombre("Lab Test");
        laboratorio.setTelefono(null);

        assertNull(laboratorio.getTelefono());
    }

    @Test
    void shouldNotEqualNull() {
        Laboratorio laboratorio = new Laboratorio(1L, "Lab Test", "Direccion", "912345678");

        assertNotEquals(null, laboratorio);
    }

    @Test
    void shouldNotEqualDifferentClass() {
        Laboratorio laboratorio = new Laboratorio(1L, "Lab Test", "Direccion", "912345678");

        assertNotEquals("String", laboratorio);
    }
}
