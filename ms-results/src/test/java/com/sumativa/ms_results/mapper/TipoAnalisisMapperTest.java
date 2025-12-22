package com.sumativa.ms_results.mapper;

import com.sumativa.ms_results.dto.TipoAnalisisCreateDto;
import com.sumativa.ms_results.dto.TipoAnalisisResponseDto;
import com.sumativa.ms_results.dto.TipoAnalisisUpdateDto;
import com.sumativa.ms_results.entity.TipoAnalisis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para TipoAnalisisMapper
 */
class TipoAnalisisMapperTest {

    private TipoAnalisisMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TipoAnalisisMapper();
    }

    // ==================== toDto Tests ====================

    @Test
    void toDto_shouldConvertEntityToDto() {
        // Arrange
        TipoAnalisis entity = new TipoAnalisis();
        entity.setId(1L);
        entity.setNombre("Glucosa");
        entity.setCategoria("Bioquímica");
        entity.setUnidadMedida("mg/dL");
        entity.setValorReferenciaMin(new BigDecimal("70.00"));
        entity.setValorReferenciaMax(new BigDecimal("100.00"));
        entity.setActivo(true);

        // Act
        TipoAnalisisResponseDto dto = mapper.toDto(entity);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Glucosa", dto.getNombre());
        assertEquals("Bioquímica", dto.getCategoria());
        assertEquals("mg/dL", dto.getUnidadMedida());
        assertEquals(new BigDecimal("70.00"), dto.getValorReferenciaMin());
        assertEquals(new BigDecimal("100.00"), dto.getValorReferenciaMax());
        assertTrue(dto.getActivo());
    }

    @Test
    void toDto_shouldReturnNull_whenEntityIsNull() {
        // Act
        TipoAnalisisResponseDto dto = mapper.toDto(null);

        // Assert
        assertNull(dto);
    }

    @Test
    void toDto_shouldHandleNullFields() {
        // Arrange
        TipoAnalisis entity = new TipoAnalisis();
        entity.setId(1L);
        entity.setNombre("Test");
        entity.setCategoria("Test Category");
        entity.setActivo(true);
        // unidadMedida, valorReferenciaMin, valorReferenciaMax are null

        // Act
        TipoAnalisisResponseDto dto = mapper.toDto(entity);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNull(dto.getUnidadMedida());
        assertNull(dto.getValorReferenciaMin());
        assertNull(dto.getValorReferenciaMax());
    }

    // ==================== toEntity Tests ====================

    @Test
    void toEntity_shouldConvertCreateDtoToEntity() {
        // Arrange
        TipoAnalisisCreateDto dto = new TipoAnalisisCreateDto();
        dto.setNombre("Hemoglobina");
        dto.setCategoria("Hematología");
        dto.setUnidadMedida("g/dL");
        dto.setValorReferenciaMin(new BigDecimal("12.00"));
        dto.setValorReferenciaMax(new BigDecimal("16.00"));
        dto.setActivo(true);

        // Act
        TipoAnalisis entity = mapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals("Hemoglobina", entity.getNombre());
        assertEquals("Hematología", entity.getCategoria());
        assertEquals("g/dL", entity.getUnidadMedida());
        assertEquals(new BigDecimal("12.00"), entity.getValorReferenciaMin());
        assertEquals(new BigDecimal("16.00"), entity.getValorReferenciaMax());
        assertTrue(entity.getActivo());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        // Act
        TipoAnalisis entity = mapper.toEntity(null);

        // Assert
        assertNull(entity);
    }

    @Test
    void toEntity_shouldSetDefaultActivoTrue_whenActivoIsNull() {
        // Arrange
        TipoAnalisisCreateDto dto = new TipoAnalisisCreateDto();
        dto.setNombre("Test");
        dto.setCategoria("Test Category");
        dto.setActivo(null);

        // Act
        TipoAnalisis entity = mapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertTrue(entity.getActivo());
    }

    @Test
    void toEntity_shouldSetActivoFalse_whenDtoHasActivoFalse() {
        // Arrange
        TipoAnalisisCreateDto dto = new TipoAnalisisCreateDto();
        dto.setNombre("Test");
        dto.setCategoria("Test Category");
        dto.setActivo(false);

        // Act
        TipoAnalisis entity = mapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertFalse(entity.getActivo());
    }

    // ==================== updateEntityFromDto Tests ====================

    @Test
    void updateEntityFromDto_shouldUpdateAllFields() {
        // Arrange
        TipoAnalisis entity = new TipoAnalisis();
        entity.setNombre("Original");
        entity.setCategoria("Original Category");
        entity.setUnidadMedida("mg/dL");
        entity.setValorReferenciaMin(new BigDecimal("10.00"));
        entity.setValorReferenciaMax(new BigDecimal("20.00"));
        entity.setActivo(true);

        TipoAnalisisUpdateDto dto = new TipoAnalisisUpdateDto();
        dto.setNombre("Updated");
        dto.setCategoria("Updated Category");
        dto.setUnidadMedida("g/L");
        dto.setValorReferenciaMin(new BigDecimal("15.00"));
        dto.setValorReferenciaMax(new BigDecimal("25.00"));
        dto.setActivo(false);

        // Act
        mapper.updateEntityFromDto(dto, entity);

        // Assert
        assertEquals("Updated", entity.getNombre());
        assertEquals("Updated Category", entity.getCategoria());
        assertEquals("g/L", entity.getUnidadMedida());
        assertEquals(new BigDecimal("15.00"), entity.getValorReferenciaMin());
        assertEquals(new BigDecimal("25.00"), entity.getValorReferenciaMax());
        assertFalse(entity.getActivo());
    }

    @Test
    void updateEntityFromDto_shouldNotUpdateNullFields() {
        // Arrange
        TipoAnalisis entity = new TipoAnalisis();
        entity.setNombre("Original");
        entity.setCategoria("Original Category");
        entity.setUnidadMedida("mg/dL");
        entity.setActivo(true);

        TipoAnalisisUpdateDto dto = new TipoAnalisisUpdateDto();
        dto.setNombre("Updated Name");
        // All other fields are null

        // Act
        mapper.updateEntityFromDto(dto, entity);

        // Assert
        assertEquals("Updated Name", entity.getNombre());
        assertEquals("Original Category", entity.getCategoria());
        assertEquals("mg/dL", entity.getUnidadMedida());
        assertTrue(entity.getActivo());
    }

    @Test
    void updateEntityFromDto_shouldDoNothing_whenDtoIsNull() {
        // Arrange
        TipoAnalisis entity = new TipoAnalisis();
        entity.setNombre("Original");

        // Act
        mapper.updateEntityFromDto(null, entity);

        // Assert
        assertEquals("Original", entity.getNombre());
    }

    @Test
    void updateEntityFromDto_shouldDoNothing_whenEntityIsNull() {
        // Arrange
        TipoAnalisisUpdateDto dto = new TipoAnalisisUpdateDto();
        dto.setNombre("Test");

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> mapper.updateEntityFromDto(dto, null));
    }

    @Test
    void updateEntityFromDto_shouldDoNothing_whenBothAreNull() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> mapper.updateEntityFromDto(null, null));
    }
}
