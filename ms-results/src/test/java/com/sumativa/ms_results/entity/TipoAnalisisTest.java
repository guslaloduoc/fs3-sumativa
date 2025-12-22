package com.sumativa.ms_results.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad TipoAnalisis
 */
class TipoAnalisisTest {

    @Test
    void constructor_withAllArgs_shouldSetAllFields() {
        // Act
        TipoAnalisis tipoAnalisis = new TipoAnalisis(
                1L,
                "Glucosa",
                "Bioquímica",
                "mg/dL",
                new BigDecimal("70.00"),
                new BigDecimal("100.00"),
                true
        );

        // Assert
        assertEquals(1L, tipoAnalisis.getId());
        assertEquals("Glucosa", tipoAnalisis.getNombre());
        assertEquals("Bioquímica", tipoAnalisis.getCategoria());
        assertEquals("mg/dL", tipoAnalisis.getUnidadMedida());
        assertEquals(new BigDecimal("70.00"), tipoAnalisis.getValorReferenciaMin());
        assertEquals(new BigDecimal("100.00"), tipoAnalisis.getValorReferenciaMax());
        assertTrue(tipoAnalisis.getActivo());
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        // Act
        TipoAnalisis tipoAnalisis = new TipoAnalisis();

        // Assert
        assertNotNull(tipoAnalisis);
        assertNull(tipoAnalisis.getId());
        assertNull(tipoAnalisis.getNombre());
        assertTrue(tipoAnalisis.getActivo()); // Default value
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        // Arrange
        TipoAnalisis tipoAnalisis = new TipoAnalisis();

        // Act
        tipoAnalisis.setId(1L);
        tipoAnalisis.setNombre("Hemoglobina");
        tipoAnalisis.setCategoria("Hematología");
        tipoAnalisis.setUnidadMedida("g/dL");
        tipoAnalisis.setValorReferenciaMin(new BigDecimal("12.00"));
        tipoAnalisis.setValorReferenciaMax(new BigDecimal("16.00"));
        tipoAnalisis.setActivo(false);

        // Assert
        assertEquals(1L, tipoAnalisis.getId());
        assertEquals("Hemoglobina", tipoAnalisis.getNombre());
        assertEquals("Hematología", tipoAnalisis.getCategoria());
        assertEquals("g/dL", tipoAnalisis.getUnidadMedida());
        assertEquals(new BigDecimal("12.00"), tipoAnalisis.getValorReferenciaMin());
        assertEquals(new BigDecimal("16.00"), tipoAnalisis.getValorReferenciaMax());
        assertFalse(tipoAnalisis.getActivo());
    }

    @Test
    void equals_shouldWorkCorrectly() {
        // Arrange
        TipoAnalisis tipo1 = new TipoAnalisis();
        tipo1.setId(1L);
        tipo1.setNombre("Glucosa");
        tipo1.setCategoria("Bioquímica");
        tipo1.setActivo(true);

        TipoAnalisis tipo2 = new TipoAnalisis();
        tipo2.setId(1L);
        tipo2.setNombre("Glucosa");
        tipo2.setCategoria("Bioquímica");
        tipo2.setActivo(true);

        // Assert
        assertEquals(tipo1, tipo2);
        assertEquals(tipo1.hashCode(), tipo2.hashCode());
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        // Arrange
        TipoAnalisis tipo1 = new TipoAnalisis();
        tipo1.setId(1L);
        tipo1.setNombre("Glucosa");

        TipoAnalisis tipo2 = new TipoAnalisis();
        tipo2.setId(2L);
        tipo2.setNombre("Glucosa");

        // Assert
        assertNotEquals(tipo1, tipo2);
    }

    @Test
    void toString_shouldNotThrowException() {
        // Arrange
        TipoAnalisis tipoAnalisis = new TipoAnalisis();
        tipoAnalisis.setId(1L);
        tipoAnalisis.setNombre("Test");

        // Act & Assert
        assertDoesNotThrow(() -> tipoAnalisis.toString());
        assertNotNull(tipoAnalisis.toString());
    }

    @Test
    void activo_defaultValue_shouldBeTrue() {
        // Arrange
        TipoAnalisis tipoAnalisis = new TipoAnalisis();

        // Assert
        assertTrue(tipoAnalisis.getActivo());
    }

    @Test
    void nullFields_shouldBeHandledCorrectly() {
        // Arrange
        TipoAnalisis tipoAnalisis = new TipoAnalisis();
        tipoAnalisis.setId(1L);
        tipoAnalisis.setNombre("Test");
        tipoAnalisis.setCategoria("Test Category");
        // unidadMedida, valorReferenciaMin, valorReferenciaMax remain null

        // Assert
        assertNull(tipoAnalisis.getUnidadMedida());
        assertNull(tipoAnalisis.getValorReferenciaMin());
        assertNull(tipoAnalisis.getValorReferenciaMax());
    }

    // ==================== Additional Branch Coverage Tests ====================

    @Test
    void equals_shouldReturnFalse_whenComparedWithNull() {
        // Arrange
        TipoAnalisis tipo = new TipoAnalisis();
        tipo.setId(1L);

        // Assert
        assertNotEquals(tipo, null);
    }

    @Test
    void equals_shouldReturnTrue_whenSameObject() {
        // Arrange
        TipoAnalisis tipo = new TipoAnalisis();
        tipo.setId(1L);

        // Assert
        assertEquals(tipo, tipo);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentClass() {
        // Arrange
        TipoAnalisis tipo = new TipoAnalisis();
        tipo.setId(1L);

        // Assert
        assertNotEquals(tipo, "Not a TipoAnalisis");
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentNombre() {
        // Arrange
        TipoAnalisis tipo1 = new TipoAnalisis();
        tipo1.setId(1L);
        tipo1.setNombre("Glucosa");

        TipoAnalisis tipo2 = new TipoAnalisis();
        tipo2.setId(1L);
        tipo2.setNombre("Hemoglobina");

        // Assert
        assertNotEquals(tipo1, tipo2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentCategoria() {
        // Arrange
        TipoAnalisis tipo1 = new TipoAnalisis();
        tipo1.setId(1L);
        tipo1.setNombre("Test");
        tipo1.setCategoria("Categoria A");

        TipoAnalisis tipo2 = new TipoAnalisis();
        tipo2.setId(1L);
        tipo2.setNombre("Test");
        tipo2.setCategoria("Categoria B");

        // Assert
        assertNotEquals(tipo1, tipo2);
    }

    @Test
    void equals_shouldHandleNullFields() {
        // Arrange
        TipoAnalisis tipo1 = new TipoAnalisis();
        tipo1.setId(null);
        tipo1.setNombre(null);

        TipoAnalisis tipo2 = new TipoAnalisis();
        tipo2.setId(null);
        tipo2.setNombre(null);

        // Assert - Both with null fields should be equal
        assertEquals(tipo1, tipo2);
    }

    @Test
    void equals_shouldReturnFalse_whenOneIdIsNull() {
        // Arrange
        TipoAnalisis tipo1 = new TipoAnalisis();
        tipo1.setId(1L);

        TipoAnalisis tipo2 = new TipoAnalisis();
        tipo2.setId(null);

        // Assert
        assertNotEquals(tipo1, tipo2);
    }

    @Test
    void hashCode_shouldBeConsistent() {
        // Arrange
        TipoAnalisis tipo = new TipoAnalisis();
        tipo.setId(1L);
        tipo.setNombre("Glucosa");

        // Act
        int hash1 = tipo.hashCode();
        int hash2 = tipo.hashCode();

        // Assert
        assertEquals(hash1, hash2);
    }

    @Test
    void hashCode_shouldHandleNullFields() {
        // Arrange
        TipoAnalisis tipo = new TipoAnalisis();
        // All fields null except activo which has default

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> tipo.hashCode());
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentActivo() {
        // Arrange
        TipoAnalisis tipo1 = new TipoAnalisis();
        tipo1.setId(1L);
        tipo1.setNombre("Test");
        tipo1.setActivo(true);

        TipoAnalisis tipo2 = new TipoAnalisis();
        tipo2.setId(1L);
        tipo2.setNombre("Test");
        tipo2.setActivo(false);

        // Assert
        assertNotEquals(tipo1, tipo2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentUnidadMedida() {
        // Arrange
        TipoAnalisis tipo1 = new TipoAnalisis();
        tipo1.setId(1L);
        tipo1.setNombre("Test");
        tipo1.setUnidadMedida("mg/dL");

        TipoAnalisis tipo2 = new TipoAnalisis();
        tipo2.setId(1L);
        tipo2.setNombre("Test");
        tipo2.setUnidadMedida("g/dL");

        // Assert
        assertNotEquals(tipo1, tipo2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentValorReferenciaMin() {
        // Arrange
        TipoAnalisis tipo1 = new TipoAnalisis();
        tipo1.setId(1L);
        tipo1.setNombre("Test");
        tipo1.setValorReferenciaMin(new BigDecimal("10.00"));

        TipoAnalisis tipo2 = new TipoAnalisis();
        tipo2.setId(1L);
        tipo2.setNombre("Test");
        tipo2.setValorReferenciaMin(new BigDecimal("20.00"));

        // Assert
        assertNotEquals(tipo1, tipo2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentValorReferenciaMax() {
        // Arrange
        TipoAnalisis tipo1 = new TipoAnalisis();
        tipo1.setId(1L);
        tipo1.setNombre("Test");
        tipo1.setValorReferenciaMax(new BigDecimal("100.00"));

        TipoAnalisis tipo2 = new TipoAnalisis();
        tipo2.setId(1L);
        tipo2.setNombre("Test");
        tipo2.setValorReferenciaMax(new BigDecimal("200.00"));

        // Assert
        assertNotEquals(tipo1, tipo2);
    }

    @Test
    void toString_shouldContainFieldValues() {
        // Arrange
        TipoAnalisis tipo = new TipoAnalisis();
        tipo.setId(1L);
        tipo.setNombre("Glucosa");
        tipo.setCategoria("Bioquimica");

        // Act
        String result = tipo.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("1"));
        assertTrue(result.contains("Glucosa"));
    }
}
