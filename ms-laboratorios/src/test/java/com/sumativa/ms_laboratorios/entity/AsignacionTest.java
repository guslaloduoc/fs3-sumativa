package com.sumativa.ms_laboratorios.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AsignacionTest {

    private Laboratorio createTestLaboratorio() {
        return new Laboratorio(1L, "Lab Test", "Direccion Test", "912345678");
    }

    @Test
    void shouldCreateAsignacionWithNoArgsConstructor() {
        Asignacion asignacion = new Asignacion();

        assertNotNull(asignacion);
        assertNull(asignacion.getId());
        assertNull(asignacion.getPaciente());
        assertNull(asignacion.getFecha());
        assertNull(asignacion.getLaboratorio());
    }

    @Test
    void shouldCreateAsignacionWithAllArgsConstructor() {
        Laboratorio lab = createTestLaboratorio();
        LocalDate fecha = LocalDate.of(2025, 1, 15);

        Asignacion asignacion = new Asignacion(1L, "Paciente Test", fecha, lab);

        assertEquals(1L, asignacion.getId());
        assertEquals("Paciente Test", asignacion.getPaciente());
        assertEquals(fecha, asignacion.getFecha());
        assertEquals(lab, asignacion.getLaboratorio());
    }

    @Test
    void shouldSetAndGetId() {
        Asignacion asignacion = new Asignacion();
        asignacion.setId(1L);

        assertEquals(1L, asignacion.getId());
    }

    @Test
    void shouldSetAndGetPaciente() {
        Asignacion asignacion = new Asignacion();
        asignacion.setPaciente("Paciente Test");

        assertEquals("Paciente Test", asignacion.getPaciente());
    }

    @Test
    void shouldSetAndGetFecha() {
        Asignacion asignacion = new Asignacion();
        LocalDate fecha = LocalDate.of(2025, 1, 15);
        asignacion.setFecha(fecha);

        assertEquals(fecha, asignacion.getFecha());
    }

    @Test
    void shouldSetAndGetLaboratorio() {
        Asignacion asignacion = new Asignacion();
        Laboratorio lab = createTestLaboratorio();
        asignacion.setLaboratorio(lab);

        assertEquals(lab, asignacion.getLaboratorio());
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        Laboratorio lab = createTestLaboratorio();
        LocalDate fecha = LocalDate.of(2025, 1, 15);

        Asignacion asig1 = new Asignacion(1L, "Paciente Test", fecha, lab);
        Asignacion asig2 = new Asignacion(1L, "Paciente Test", fecha, lab);
        Asignacion asig3 = new Asignacion(2L, "Otro Paciente", fecha, lab);

        assertEquals(asig1, asig2);
        assertNotEquals(asig1, asig3);
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        Laboratorio lab = createTestLaboratorio();
        LocalDate fecha = LocalDate.of(2025, 1, 15);

        Asignacion asig1 = new Asignacion(1L, "Paciente Test", fecha, lab);
        Asignacion asig2 = new Asignacion(1L, "Paciente Test", fecha, lab);

        assertEquals(asig1.hashCode(), asig2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        Laboratorio lab = createTestLaboratorio();
        LocalDate fecha = LocalDate.of(2025, 1, 15);
        Asignacion asignacion = new Asignacion(1L, "Paciente Test", fecha, lab);

        String toString = asignacion.toString();

        assertTrue(toString.contains("Paciente Test"));
        assertTrue(toString.contains("2025-01-15"));
    }

    @Test
    void shouldNotEqualNull() {
        Asignacion asignacion = new Asignacion(1L, "Paciente", LocalDate.now(), createTestLaboratorio());

        assertNotEquals(null, asignacion);
    }

    @Test
    void shouldNotEqualDifferentClass() {
        Asignacion asignacion = new Asignacion(1L, "Paciente", LocalDate.now(), createTestLaboratorio());

        assertNotEquals("String", asignacion);
    }

    @Test
    void shouldHandleNullLaboratorio() {
        Asignacion asignacion = new Asignacion();
        asignacion.setId(1L);
        asignacion.setPaciente("Paciente");
        asignacion.setFecha(LocalDate.now());
        asignacion.setLaboratorio(null);

        assertNull(asignacion.getLaboratorio());
    }
}
