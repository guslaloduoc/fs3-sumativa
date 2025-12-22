package com.sumativa.ms_results.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sumativa.ms_results.dto.TipoAnalisisCreateDto;
import com.sumativa.ms_results.dto.TipoAnalisisResponseDto;
import com.sumativa.ms_results.dto.TipoAnalisisUpdateDto;
import com.sumativa.ms_results.entity.TipoAnalisis;
import com.sumativa.ms_results.mapper.TipoAnalisisMapper;
import com.sumativa.ms_results.service.TipoAnalisisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para TipoAnalisisController
 */
@ExtendWith(MockitoExtension.class)
class TipoAnalisisControllerTest {

    @Mock
    private TipoAnalisisService tipoAnalisisService;

    @Mock
    private TipoAnalisisMapper tipoAnalisisMapper;

    @InjectMocks
    private TipoAnalisisController tipoAnalisisController;

    private ObjectMapper objectMapper;
    private TipoAnalisis testTipoAnalisis;
    private TipoAnalisisResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testTipoAnalisis = new TipoAnalisis();
        testTipoAnalisis.setId(1L);
        testTipoAnalisis.setNombre("Glucosa");
        testTipoAnalisis.setCategoria("Bioquímica");
        testTipoAnalisis.setUnidadMedida("mg/dL");
        testTipoAnalisis.setValorReferenciaMin(new BigDecimal("70.00"));
        testTipoAnalisis.setValorReferenciaMax(new BigDecimal("100.00"));
        testTipoAnalisis.setActivo(true);

        testResponseDto = TipoAnalisisResponseDto.builder()
                .id(1L)
                .nombre("Glucosa")
                .categoria("Bioquímica")
                .unidadMedida("mg/dL")
                .valorReferenciaMin(new BigDecimal("70.00"))
                .valorReferenciaMax(new BigDecimal("100.00"))
                .activo(true)
                .build();
    }

    // ==================== getAll Tests ====================

    @Test
    void getAll_shouldReturnListOfTipoAnalisis() {
        // Arrange
        TipoAnalisis tipo2 = new TipoAnalisis();
        tipo2.setId(2L);
        tipo2.setNombre("Hemoglobina");

        TipoAnalisisResponseDto dto2 = TipoAnalisisResponseDto.builder()
                .id(2L)
                .nombre("Hemoglobina")
                .build();

        when(tipoAnalisisService.findAll()).thenReturn(Arrays.asList(testTipoAnalisis, tipo2));
        when(tipoAnalisisMapper.toDto(testTipoAnalisis)).thenReturn(testResponseDto);
        when(tipoAnalisisMapper.toDto(tipo2)).thenReturn(dto2);

        // Act
        ResponseEntity<List<TipoAnalisisResponseDto>> response = tipoAnalisisController.getAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(tipoAnalisisService).findAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoTiposExist() {
        // Arrange
        when(tipoAnalisisService.findAll()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<TipoAnalisisResponseDto>> response = tipoAnalisisController.getAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // ==================== getById Tests ====================

    @Test
    void getById_shouldReturnTipoAnalisis_whenExists() {
        // Arrange
        when(tipoAnalisisService.findById(1L)).thenReturn(testTipoAnalisis);
        when(tipoAnalisisMapper.toDto(testTipoAnalisis)).thenReturn(testResponseDto);

        // Act
        ResponseEntity<TipoAnalisisResponseDto> response = tipoAnalisisController.getById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Glucosa", response.getBody().getNombre());
        verify(tipoAnalisisService).findById(1L);
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        // Arrange
        when(tipoAnalisisService.findById(999L)).thenThrow(
                new IllegalArgumentException("Tipo de análisis no encontrado con ID: 999"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            tipoAnalisisController.getById(999L);
        });
    }

    // ==================== create Tests ====================

    @Test
    void create_shouldReturnCreatedTipoAnalisis() {
        // Arrange
        TipoAnalisisCreateDto createDto = new TipoAnalisisCreateDto();
        createDto.setNombre("Colesterol");
        createDto.setCategoria("Lípidos");
        createDto.setActivo(true);

        TipoAnalisis created = new TipoAnalisis();
        created.setId(2L);
        created.setNombre("Colesterol");

        TipoAnalisisResponseDto responseDto = TipoAnalisisResponseDto.builder()
                .id(2L)
                .nombre("Colesterol")
                .categoria("Lípidos")
                .activo(true)
                .build();

        when(tipoAnalisisMapper.toEntity(createDto)).thenReturn(created);
        when(tipoAnalisisService.create(any(TipoAnalisis.class))).thenReturn(created);
        when(tipoAnalisisMapper.toDto(created)).thenReturn(responseDto);

        // Act
        ResponseEntity<TipoAnalisisResponseDto> response = tipoAnalisisController.create(createDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        assertEquals("Colesterol", response.getBody().getNombre());
        verify(tipoAnalisisService).create(any(TipoAnalisis.class));
    }

    // ==================== update Tests ====================

    @Test
    void update_shouldReturnUpdatedTipoAnalisis() {
        // Arrange
        TipoAnalisisUpdateDto updateDto = new TipoAnalisisUpdateDto();
        updateDto.setNombre("Glucosa Actualizada");
        updateDto.setCategoria("Bioquímica Avanzada");

        TipoAnalisis updated = new TipoAnalisis();
        updated.setId(1L);
        updated.setNombre("Glucosa Actualizada");
        updated.setCategoria("Bioquímica Avanzada");

        TipoAnalisisResponseDto responseDto = TipoAnalisisResponseDto.builder()
                .id(1L)
                .nombre("Glucosa Actualizada")
                .categoria("Bioquímica Avanzada")
                .build();

        when(tipoAnalisisService.findById(1L)).thenReturn(testTipoAnalisis);
        doNothing().when(tipoAnalisisMapper).updateEntityFromDto(updateDto, testTipoAnalisis);
        when(tipoAnalisisService.update(anyLong(), any(TipoAnalisis.class))).thenReturn(updated);
        when(tipoAnalisisMapper.toDto(updated)).thenReturn(responseDto);

        // Act
        ResponseEntity<TipoAnalisisResponseDto> response = tipoAnalisisController.update(1L, updateDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Glucosa Actualizada", response.getBody().getNombre());
        verify(tipoAnalisisService).update(anyLong(), any(TipoAnalisis.class));
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        // Arrange
        TipoAnalisisUpdateDto updateDto = new TipoAnalisisUpdateDto();
        updateDto.setNombre("Test");

        when(tipoAnalisisService.findById(999L)).thenThrow(
                new IllegalArgumentException("Tipo de análisis no encontrado con ID: 999"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            tipoAnalisisController.update(999L, updateDto);
        });
    }

    // ==================== delete Tests ====================

    @Test
    void delete_shouldReturnNoContent_whenSuccessful() {
        // Arrange
        doNothing().when(tipoAnalisisService).delete(1L);

        // Act
        ResponseEntity<Void> response = tipoAnalisisController.delete(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(tipoAnalisisService).delete(1L);
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        // Arrange
        doThrow(new IllegalArgumentException("Tipo de análisis no encontrado con ID: 999"))
                .when(tipoAnalisisService).delete(999L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            tipoAnalisisController.delete(999L);
        });
    }
}
