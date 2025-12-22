package com.sumativa.ms_results.service;

import com.sumativa.ms_results.entity.TipoAnalisis;
import com.sumativa.ms_results.repository.TipoAnalisisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para TipoAnalisisService
 */
@ExtendWith(MockitoExtension.class)
class TipoAnalisisServiceTest {

    @Mock
    private TipoAnalisisRepository tipoAnalisisRepository;

    @InjectMocks
    private TipoAnalisisService tipoAnalisisService;

    private TipoAnalisis testTipoAnalisis;

    @BeforeEach
    void setUp() {
        testTipoAnalisis = new TipoAnalisis();
        testTipoAnalisis.setId(1L);
        testTipoAnalisis.setNombre("Glucosa");
        testTipoAnalisis.setCategoria("Bioquímica");
        testTipoAnalisis.setUnidadMedida("mg/dL");
        testTipoAnalisis.setValorReferenciaMin(new BigDecimal("70.00"));
        testTipoAnalisis.setValorReferenciaMax(new BigDecimal("100.00"));
        testTipoAnalisis.setActivo(true);
    }

    // ==================== findAll Tests ====================

    @Test
    void findAll_shouldReturnListOfTipoAnalisis() {
        // Arrange
        TipoAnalisis tipo2 = new TipoAnalisis();
        tipo2.setId(2L);
        tipo2.setNombre("Hemoglobina");
        tipo2.setCategoria("Hematología");

        when(tipoAnalisisRepository.findAll()).thenReturn(Arrays.asList(testTipoAnalisis, tipo2));

        // Act
        List<TipoAnalisis> result = tipoAnalisisService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(tipoAnalisisRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTipoAnalisisExist() {
        // Arrange
        when(tipoAnalisisRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<TipoAnalisis> result = tipoAnalisisService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tipoAnalisisRepository).findAll();
    }

    // ==================== findById Tests ====================

    @Test
    void findById_shouldReturnTipoAnalisis_whenExists() {
        // Arrange
        when(tipoAnalisisRepository.findById(1L)).thenReturn(Optional.of(testTipoAnalisis));

        // Act
        TipoAnalisis result = tipoAnalisisService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Glucosa", result.getNombre());
        verify(tipoAnalisisRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        // Arrange
        when(tipoAnalisisRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tipoAnalisisService.findById(999L);
        });

        assertTrue(exception.getMessage().contains("Tipo de análisis no encontrado"));
        assertTrue(exception.getMessage().contains("999"));
        verify(tipoAnalisisRepository).findById(999L);
    }

    // ==================== create Tests ====================

    @Test
    void create_shouldSaveTipoAnalisis_whenNameIsUnique() {
        // Arrange
        TipoAnalisis newTipo = new TipoAnalisis();
        newTipo.setNombre("Colesterol");
        newTipo.setCategoria("Lípidos");
        newTipo.setActivo(true);

        when(tipoAnalisisRepository.findByNombreIgnoreCase("Colesterol")).thenReturn(Optional.empty());
        when(tipoAnalisisRepository.save(any(TipoAnalisis.class))).thenReturn(newTipo);

        // Act
        TipoAnalisis result = tipoAnalisisService.create(newTipo);

        // Assert
        assertNotNull(result);
        assertEquals("Colesterol", result.getNombre());
        verify(tipoAnalisisRepository).findByNombreIgnoreCase("Colesterol");
        verify(tipoAnalisisRepository).save(newTipo);
    }

    @Test
    void create_shouldThrowException_whenNameAlreadyExists() {
        // Arrange
        TipoAnalisis newTipo = new TipoAnalisis();
        newTipo.setNombre("Glucosa");
        newTipo.setCategoria("Bioquímica");

        when(tipoAnalisisRepository.findByNombreIgnoreCase("Glucosa")).thenReturn(Optional.of(testTipoAnalisis));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tipoAnalisisService.create(newTipo);
        });

        assertTrue(exception.getMessage().contains("Ya existe un tipo de análisis con el nombre"));
        assertTrue(exception.getMessage().contains("Glucosa"));
        verify(tipoAnalisisRepository).findByNombreIgnoreCase("Glucosa");
        verify(tipoAnalisisRepository, never()).save(any());
    }

    // ==================== update Tests ====================

    @Test
    void update_shouldUpdateTipoAnalisis_whenExists() {
        // Arrange
        TipoAnalisis updateData = new TipoAnalisis();
        updateData.setNombre("Glucosa Actualizada");
        updateData.setCategoria("Bioquímica Avanzada");
        updateData.setUnidadMedida("mmol/L");
        updateData.setValorReferenciaMin(new BigDecimal("4.0"));
        updateData.setValorReferenciaMax(new BigDecimal("6.0"));
        updateData.setActivo(false);

        when(tipoAnalisisRepository.findById(1L)).thenReturn(Optional.of(testTipoAnalisis));
        when(tipoAnalisisRepository.findByNombreIgnoreCase("Glucosa Actualizada")).thenReturn(Optional.empty());
        when(tipoAnalisisRepository.save(any(TipoAnalisis.class))).thenReturn(testTipoAnalisis);

        // Act
        TipoAnalisis result = tipoAnalisisService.update(1L, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("Glucosa Actualizada", testTipoAnalisis.getNombre());
        assertEquals("Bioquímica Avanzada", testTipoAnalisis.getCategoria());
        verify(tipoAnalisisRepository).findById(1L);
        verify(tipoAnalisisRepository).save(any(TipoAnalisis.class));
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        // Arrange
        TipoAnalisis updateData = new TipoAnalisis();
        updateData.setNombre("Test");

        when(tipoAnalisisRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tipoAnalisisService.update(999L, updateData);
        });

        assertTrue(exception.getMessage().contains("Tipo de análisis no encontrado"));
        verify(tipoAnalisisRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowException_whenNameExistsForAnotherRecord() {
        // Arrange
        TipoAnalisis existing = new TipoAnalisis();
        existing.setId(2L);
        existing.setNombre("Hemoglobina");

        TipoAnalisis updateData = new TipoAnalisis();
        updateData.setNombre("Hemoglobina");
        updateData.setCategoria("Test");

        when(tipoAnalisisRepository.findById(1L)).thenReturn(Optional.of(testTipoAnalisis));
        when(tipoAnalisisRepository.findByNombreIgnoreCase("Hemoglobina")).thenReturn(Optional.of(existing));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tipoAnalisisService.update(1L, updateData);
        });

        assertTrue(exception.getMessage().contains("Ya existe otro tipo de análisis con el nombre"));
        verify(tipoAnalisisRepository, never()).save(any());
    }

    @Test
    void update_shouldAllowSameName_whenUpdatingSameRecord() {
        // Arrange
        TipoAnalisis updateData = new TipoAnalisis();
        updateData.setNombre("Glucosa");
        updateData.setCategoria("Nueva Categoría");
        updateData.setUnidadMedida("mg/dL");
        updateData.setActivo(true);

        when(tipoAnalisisRepository.findById(1L)).thenReturn(Optional.of(testTipoAnalisis));
        when(tipoAnalisisRepository.findByNombreIgnoreCase("Glucosa")).thenReturn(Optional.of(testTipoAnalisis));
        when(tipoAnalisisRepository.save(any(TipoAnalisis.class))).thenReturn(testTipoAnalisis);

        // Act
        TipoAnalisis result = tipoAnalisisService.update(1L, updateData);

        // Assert
        assertNotNull(result);
        verify(tipoAnalisisRepository).save(any(TipoAnalisis.class));
    }

    // ==================== delete Tests ====================

    @Test
    void delete_shouldDeleteTipoAnalisis_whenExists() {
        // Arrange
        when(tipoAnalisisRepository.findById(1L)).thenReturn(Optional.of(testTipoAnalisis));
        doNothing().when(tipoAnalisisRepository).delete(testTipoAnalisis);

        // Act
        assertDoesNotThrow(() -> tipoAnalisisService.delete(1L));

        // Assert
        verify(tipoAnalisisRepository).findById(1L);
        verify(tipoAnalisisRepository).delete(testTipoAnalisis);
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        // Arrange
        when(tipoAnalisisRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tipoAnalisisService.delete(999L);
        });

        assertTrue(exception.getMessage().contains("Tipo de análisis no encontrado"));
        verify(tipoAnalisisRepository).findById(999L);
        verify(tipoAnalisisRepository, never()).delete(any());
    }
}
