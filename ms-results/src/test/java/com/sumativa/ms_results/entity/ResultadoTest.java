package com.sumativa.ms_results.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad Resultado
 */
class ResultadoTest {

    @Test
    void onCreate_shouldSetTimestamps() {
        // Arrange
        Resultado resultado = new Resultado();
        resultado.setPaciente("Test Patient");
        resultado.setEstado("PENDIENTE");

        // Act
        resultado.onCreate();

        // Assert
        assertNotNull(resultado.getCreadoEn());
        assertNotNull(resultado.getActualizadoEn());
        assertEquals(resultado.getCreadoEn(), resultado.getActualizadoEn());
    }

    @Test
    void onUpdate_shouldUpdateActualizadoEn() throws InterruptedException {
        // Arrange
        Resultado resultado = new Resultado();
        resultado.onCreate();
        LocalDateTime originalActualizadoEn = resultado.getActualizadoEn();

        // Wait a bit to ensure different timestamp
        Thread.sleep(10);

        // Act
        resultado.onUpdate();

        // Assert
        assertNotNull(resultado.getActualizadoEn());
        assertTrue(resultado.getActualizadoEn().isAfter(originalActualizadoEn) ||
                   resultado.getActualizadoEn().isEqual(originalActualizadoEn));
    }

    @Test
    void constructor_withAllArgs_shouldSetAllFields() {
        // Arrange
        TipoAnalisis tipoAnalisis = new TipoAnalisis();
        tipoAnalisis.setId(1L);
        tipoAnalisis.setNombre("Glucosa");

        LocalDateTime fechaRealizacion = LocalDateTime.of(2025, 1, 15, 10, 0);
        LocalDateTime creadoEn = LocalDateTime.of(2025, 1, 15, 9, 0);
        LocalDateTime actualizadoEn = LocalDateTime.of(2025, 1, 15, 10, 0);

        // Act
        Resultado resultado = new Resultado(
                1L,
                "Juan Pérez",
                fechaRealizacion,
                tipoAnalisis,
                100L,
                new BigDecimal("85.00"),
                "Normal",
                "COMPLETADO",
                "Sin observaciones",
                creadoEn,
                actualizadoEn
        );

        // Assert
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Pérez", resultado.getPaciente());
        assertEquals(fechaRealizacion, resultado.getFechaRealizacion());
        assertEquals(tipoAnalisis, resultado.getTipoAnalisis());
        assertEquals(100L, resultado.getLaboratorioId());
        assertEquals(new BigDecimal("85.00"), resultado.getValorNumerico());
        assertEquals("Normal", resultado.getValorTexto());
        assertEquals("COMPLETADO", resultado.getEstado());
        assertEquals("Sin observaciones", resultado.getObservaciones());
        assertEquals(creadoEn, resultado.getCreadoEn());
        assertEquals(actualizadoEn, resultado.getActualizadoEn());
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        // Act
        Resultado resultado = new Resultado();

        // Assert
        assertNotNull(resultado);
        assertNull(resultado.getId());
        assertNull(resultado.getPaciente());
        assertEquals("PENDIENTE", resultado.getEstado()); // Default value
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        // Arrange
        Resultado resultado = new Resultado();

        // Act
        resultado.setId(1L);
        resultado.setPaciente("Test Patient");
        resultado.setFechaRealizacion(LocalDateTime.now());
        resultado.setLaboratorioId(100L);
        resultado.setValorNumerico(new BigDecimal("90.00"));
        resultado.setValorTexto("Normal");
        resultado.setEstado("COMPLETADO");
        resultado.setObservaciones("Test observations");

        // Assert
        assertEquals(1L, resultado.getId());
        assertEquals("Test Patient", resultado.getPaciente());
        assertNotNull(resultado.getFechaRealizacion());
        assertEquals(100L, resultado.getLaboratorioId());
        assertEquals(new BigDecimal("90.00"), resultado.getValorNumerico());
        assertEquals("Normal", resultado.getValorTexto());
        assertEquals("COMPLETADO", resultado.getEstado());
        assertEquals("Test observations", resultado.getObservaciones());
    }

    @Test
    void equals_shouldWorkCorrectly() {
        // Arrange
        Resultado resultado1 = new Resultado();
        resultado1.setId(1L);
        resultado1.setPaciente("Patient");

        Resultado resultado2 = new Resultado();
        resultado2.setId(1L);
        resultado2.setPaciente("Patient");

        // Assert
        assertEquals(resultado1, resultado2);
        assertEquals(resultado1.hashCode(), resultado2.hashCode());
    }

    @Test
    void toString_shouldNotThrowException() {
        // Arrange
        Resultado resultado = new Resultado();
        resultado.setId(1L);
        resultado.setPaciente("Test");

        // Act & Assert
        assertDoesNotThrow(() -> resultado.toString());
        assertNotNull(resultado.toString());
    }

    // ==================== Additional Branch Coverage Tests ====================

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        // Arrange
        Resultado resultado1 = new Resultado();
        resultado1.setId(1L);
        resultado1.setPaciente("Patient");

        Resultado resultado2 = new Resultado();
        resultado2.setId(2L);
        resultado2.setPaciente("Patient");

        // Assert
        assertNotEquals(resultado1, resultado2);
    }

    @Test
    void equals_shouldReturnFalse_whenComparedWithNull() {
        // Arrange
        Resultado resultado = new Resultado();
        resultado.setId(1L);

        // Assert
        assertNotEquals(resultado, null);
    }

    @Test
    void equals_shouldReturnTrue_whenSameObject() {
        // Arrange
        Resultado resultado = new Resultado();
        resultado.setId(1L);

        // Assert
        assertEquals(resultado, resultado);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentClass() {
        // Arrange
        Resultado resultado = new Resultado();
        resultado.setId(1L);

        // Assert
        assertNotEquals(resultado, "Not a Resultado");
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentPaciente() {
        // Arrange
        Resultado resultado1 = new Resultado();
        resultado1.setId(1L);
        resultado1.setPaciente("Patient A");

        Resultado resultado2 = new Resultado();
        resultado2.setId(1L);
        resultado2.setPaciente("Patient B");

        // Assert
        assertNotEquals(resultado1, resultado2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentEstado() {
        // Arrange
        Resultado resultado1 = new Resultado();
        resultado1.setId(1L);
        resultado1.setPaciente("Patient");
        resultado1.setEstado("PENDIENTE");

        Resultado resultado2 = new Resultado();
        resultado2.setId(1L);
        resultado2.setPaciente("Patient");
        resultado2.setEstado("COMPLETADO");

        // Assert
        assertNotEquals(resultado1, resultado2);
    }

    @Test
    void equals_shouldHandleNullFields() {
        // Arrange
        Resultado resultado1 = new Resultado();
        resultado1.setId(null);
        resultado1.setPaciente(null);

        Resultado resultado2 = new Resultado();
        resultado2.setId(null);
        resultado2.setPaciente(null);

        // Assert - Both with null fields should be equal
        assertEquals(resultado1, resultado2);
    }

    @Test
    void equals_shouldReturnFalse_whenOneIdIsNull() {
        // Arrange
        Resultado resultado1 = new Resultado();
        resultado1.setId(1L);

        Resultado resultado2 = new Resultado();
        resultado2.setId(null);

        // Assert
        assertNotEquals(resultado1, resultado2);
    }

    @Test
    void hashCode_shouldBeConsistent() {
        // Arrange
        Resultado resultado = new Resultado();
        resultado.setId(1L);
        resultado.setPaciente("Patient");

        // Act
        int hash1 = resultado.hashCode();
        int hash2 = resultado.hashCode();

        // Assert
        assertEquals(hash1, hash2);
    }

    @Test
    void hashCode_shouldHandleNullFields() {
        // Arrange
        Resultado resultado = new Resultado();
        // All fields null except estado which has default

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> resultado.hashCode());
    }

    @Test
    void tipoAnalisis_shouldBeSettable() {
        // Arrange
        Resultado resultado = new Resultado();
        TipoAnalisis tipoAnalisis = new TipoAnalisis();
        tipoAnalisis.setId(1L);
        tipoAnalisis.setNombre("Hemograma");

        // Act
        resultado.setTipoAnalisis(tipoAnalisis);

        // Assert
        assertNotNull(resultado.getTipoAnalisis());
        assertEquals(1L, resultado.getTipoAnalisis().getId());
        assertEquals("Hemograma", resultado.getTipoAnalisis().getNombre());
    }

    @Test
    void estado_defaultValue_shouldBePendiente() {
        // Arrange
        Resultado resultado = new Resultado();

        // Assert
        assertEquals("PENDIENTE", resultado.getEstado());
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentLaboratorioId() {
        // Arrange
        Resultado resultado1 = new Resultado();
        resultado1.setId(1L);
        resultado1.setLaboratorioId(100L);

        Resultado resultado2 = new Resultado();
        resultado2.setId(1L);
        resultado2.setLaboratorioId(200L);

        // Assert
        assertNotEquals(resultado1, resultado2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentValorNumerico() {
        // Arrange
        Resultado resultado1 = new Resultado();
        resultado1.setId(1L);
        resultado1.setValorNumerico(new BigDecimal("100.00"));

        Resultado resultado2 = new Resultado();
        resultado2.setId(1L);
        resultado2.setValorNumerico(new BigDecimal("200.00"));

        // Assert
        assertNotEquals(resultado1, resultado2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentFechaRealizacion() {
        // Arrange
        Resultado resultado1 = new Resultado();
        resultado1.setId(1L);
        resultado1.setFechaRealizacion(LocalDateTime.of(2025, 1, 1, 10, 0));

        Resultado resultado2 = new Resultado();
        resultado2.setId(1L);
        resultado2.setFechaRealizacion(LocalDateTime.of(2025, 2, 1, 10, 0));

        // Assert
        assertNotEquals(resultado1, resultado2);
    }
}
