package com.sumativa.ms_results.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sumativa.ms_results.dto.ResultadoCreateDto;
import com.sumativa.ms_results.dto.ResultadoResponseDto;
import com.sumativa.ms_results.dto.ResultadoUpdateDto;
import com.sumativa.ms_results.dto.TipoAnalisisResponseDto;
import com.sumativa.ms_results.entity.Resultado;
import com.sumativa.ms_results.entity.TipoAnalisis;
import com.sumativa.ms_results.mapper.ResultadoMapper;
import com.sumativa.ms_results.service.ResultadoService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ResultadoController
 */
@ExtendWith(MockitoExtension.class)
class ResultadoControllerTest {

    @Mock
    private ResultadoService resultadoService;

    @Mock
    private TipoAnalisisService tipoAnalisisService;

    @Mock
    private ResultadoMapper resultadoMapper;

    @InjectMocks
    private ResultadoController resultadoController;

    private ObjectMapper objectMapper;
    private Resultado testResultado;
    private TipoAnalisis testTipoAnalisis;
    private ResultadoResponseDto testResponseDto;
    private TipoAnalisisResponseDto tipoAnalisisResponseDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testTipoAnalisis = new TipoAnalisis();
        testTipoAnalisis.setId(1L);
        testTipoAnalisis.setNombre("Glucosa");
        testTipoAnalisis.setCategoria("Bioquímica");
        testTipoAnalisis.setActivo(true);

        testResultado = new Resultado();
        testResultado.setId(1L);
        testResultado.setPaciente("Juan Pérez");
        testResultado.setFechaRealizacion(LocalDateTime.of(2025, 1, 15, 10, 0));
        testResultado.setTipoAnalisis(testTipoAnalisis);
        testResultado.setLaboratorioId(100L);
        testResultado.setValorNumerico(new BigDecimal("85.00"));
        testResultado.setEstado("COMPLETADO");

        tipoAnalisisResponseDto = TipoAnalisisResponseDto.builder()
                .id(1L)
                .nombre("Glucosa")
                .categoria("Bioquímica")
                .activo(true)
                .build();

        testResponseDto = ResultadoResponseDto.builder()
                .id(1L)
                .paciente("Juan Pérez")
                .fechaRealizacion(LocalDateTime.of(2025, 1, 15, 10, 0))
                .tipoAnalisis(tipoAnalisisResponseDto)
                .laboratorioId(100L)
                .valorNumerico(new BigDecimal("85.00"))
                .estado("COMPLETADO")
                .build();
    }

    // ==================== getAll Tests ====================

    @Test
    void getAll_shouldReturnListOfResultados() {
        // Arrange
        Resultado resultado2 = new Resultado();
        resultado2.setId(2L);
        resultado2.setPaciente("María García");

        ResultadoResponseDto dto2 = ResultadoResponseDto.builder()
                .id(2L)
                .paciente("María García")
                .build();

        when(resultadoService.findAll()).thenReturn(Arrays.asList(testResultado, resultado2));
        when(resultadoMapper.toDto(testResultado)).thenReturn(testResponseDto);
        when(resultadoMapper.toDto(resultado2)).thenReturn(dto2);

        // Act
        ResponseEntity<List<ResultadoResponseDto>> response = resultadoController.getAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(resultadoService).findAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoResultadosExist() {
        // Arrange
        when(resultadoService.findAll()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<ResultadoResponseDto>> response = resultadoController.getAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // ==================== getById Tests ====================

    @Test
    void getById_shouldReturnResultado_whenExists() {
        // Arrange
        when(resultadoService.findById(1L)).thenReturn(testResultado);
        when(resultadoMapper.toDto(testResultado)).thenReturn(testResponseDto);

        // Act
        ResponseEntity<ResultadoResponseDto> response = resultadoController.getById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Juan Pérez", response.getBody().getPaciente());
        verify(resultadoService).findById(1L);
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        // Arrange
        when(resultadoService.findById(999L)).thenThrow(
                new IllegalArgumentException("Resultado no encontrado con ID: 999"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            resultadoController.getById(999L);
        });
    }

    // ==================== create Tests ====================

    @Test
    void create_shouldReturnCreatedResultado() {
        // Arrange
        ResultadoCreateDto createDto = ResultadoCreateDto.builder()
                .paciente("Nuevo Paciente")
                .fechaRealizacion(LocalDateTime.of(2025, 1, 20, 14, 0))
                .tipoAnalisisId(1L)
                .laboratorioId(100L)
                .valorNumerico(new BigDecimal("90.00"))
                .estado("PENDIENTE")
                .build();

        Resultado created = new Resultado();
        created.setId(2L);
        created.setPaciente("Nuevo Paciente");
        created.setTipoAnalisis(testTipoAnalisis);

        ResultadoResponseDto responseDto = ResultadoResponseDto.builder()
                .id(2L)
                .paciente("Nuevo Paciente")
                .tipoAnalisis(tipoAnalisisResponseDto)
                .laboratorioId(100L)
                .estado("PENDIENTE")
                .build();

        when(tipoAnalisisService.findById(1L)).thenReturn(testTipoAnalisis);
        when(resultadoMapper.toEntity(createDto, testTipoAnalisis)).thenReturn(created);
        when(resultadoService.create(any(Resultado.class))).thenReturn(created);
        when(resultadoMapper.toDto(created)).thenReturn(responseDto);

        // Act
        ResponseEntity<ResultadoResponseDto> response = resultadoController.create(createDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        assertEquals("Nuevo Paciente", response.getBody().getPaciente());
        verify(resultadoService).create(any(Resultado.class));
    }

    @Test
    void create_shouldThrowException_whenTipoAnalisisNotFound() {
        // Arrange
        ResultadoCreateDto createDto = ResultadoCreateDto.builder()
                .paciente("Test")
                .fechaRealizacion(LocalDateTime.now())
                .tipoAnalisisId(999L)
                .laboratorioId(100L)
                .estado("PENDIENTE")
                .build();

        when(tipoAnalisisService.findById(999L)).thenThrow(
                new IllegalArgumentException("Tipo de análisis no encontrado con ID: 999"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            resultadoController.create(createDto);
        });
    }

    // ==================== update Tests ====================

    @Test
    void update_shouldReturnUpdatedResultado() {
        // Arrange
        ResultadoUpdateDto updateDto = new ResultadoUpdateDto();
        updateDto.setPaciente("Paciente Actualizado");
        updateDto.setEstado("REVISADO");

        Resultado updated = new Resultado();
        updated.setId(1L);
        updated.setPaciente("Paciente Actualizado");
        updated.setEstado("REVISADO");

        ResultadoResponseDto responseDto = ResultadoResponseDto.builder()
                .id(1L)
                .paciente("Paciente Actualizado")
                .estado("REVISADO")
                .build();

        when(resultadoService.findById(1L)).thenReturn(testResultado);
        doNothing().when(resultadoMapper).updateEntityFromDto(any(), any(), any());
        when(resultadoService.update(anyLong(), any(Resultado.class))).thenReturn(updated);
        when(resultadoMapper.toDto(updated)).thenReturn(responseDto);

        // Act
        ResponseEntity<ResultadoResponseDto> response = resultadoController.update(1L, updateDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Paciente Actualizado", response.getBody().getPaciente());
        verify(resultadoService).update(anyLong(), any(Resultado.class));
    }

    @Test
    void update_shouldFetchNewTipoAnalisis_whenIdProvided() {
        // Arrange
        TipoAnalisis newTipo = new TipoAnalisis();
        newTipo.setId(2L);
        newTipo.setNombre("Colesterol");

        ResultadoUpdateDto updateDto = new ResultadoUpdateDto();
        updateDto.setPaciente("Test");
        updateDto.setTipoAnalisisId(2L);

        when(resultadoService.findById(1L)).thenReturn(testResultado);
        when(tipoAnalisisService.findById(2L)).thenReturn(newTipo);
        doNothing().when(resultadoMapper).updateEntityFromDto(any(), any(), any());
        when(resultadoService.update(anyLong(), any(Resultado.class))).thenReturn(testResultado);
        when(resultadoMapper.toDto(any())).thenReturn(testResponseDto);

        // Act
        ResponseEntity<ResultadoResponseDto> response = resultadoController.update(1L, updateDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(tipoAnalisisService).findById(2L);
    }

    @Test
    void update_shouldNotFetchTipoAnalisis_whenIdNotProvided() {
        // Arrange
        ResultadoUpdateDto updateDto = new ResultadoUpdateDto();
        updateDto.setPaciente("Test");
        updateDto.setTipoAnalisisId(null);

        when(resultadoService.findById(1L)).thenReturn(testResultado);
        doNothing().when(resultadoMapper).updateEntityFromDto(any(), any(), any());
        when(resultadoService.update(anyLong(), any(Resultado.class))).thenReturn(testResultado);
        when(resultadoMapper.toDto(any())).thenReturn(testResponseDto);

        // Act
        ResponseEntity<ResultadoResponseDto> response = resultadoController.update(1L, updateDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(tipoAnalisisService, never()).findById(anyLong());
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        // Arrange
        ResultadoUpdateDto updateDto = new ResultadoUpdateDto();
        updateDto.setPaciente("Test");

        when(resultadoService.findById(999L)).thenThrow(
                new IllegalArgumentException("Resultado no encontrado con ID: 999"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            resultadoController.update(999L, updateDto);
        });
    }

    // ==================== delete Tests ====================

    @Test
    void delete_shouldReturnNoContent_whenSuccessful() {
        // Arrange
        doNothing().when(resultadoService).delete(1L);

        // Act
        ResponseEntity<Void> response = resultadoController.delete(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(resultadoService).delete(1L);
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        // Arrange
        doThrow(new IllegalArgumentException("Resultado no encontrado con ID: 999"))
                .when(resultadoService).delete(999L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            resultadoController.delete(999L);
        });
    }
}
