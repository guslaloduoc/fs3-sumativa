package com.sumativa.ms_results.service;

import com.sumativa.ms_results.entity.Resultado;
import com.sumativa.ms_results.entity.TipoAnalisis;
import com.sumativa.ms_results.repository.ResultadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ResultadoService
 */
@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

    @Mock
    private ResultadoRepository resultadoRepository;

    @Mock
    private TipoAnalisisService tipoAnalisisService;

    @InjectMocks
    private ResultadoService resultadoService;

    private Resultado testResultado;
    private TipoAnalisis testTipoAnalisis;

    @BeforeEach
    void setUp() {
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
    }

    // ==================== findAll Tests ====================

    @Test
    void findAll_shouldReturnListOfResultados() {
        // Arrange
        Resultado resultado2 = new Resultado();
        resultado2.setId(2L);
        resultado2.setPaciente("María García");

        when(resultadoRepository.findAll()).thenReturn(Arrays.asList(testResultado, resultado2));

        // Act
        List<Resultado> result = resultadoService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(resultadoRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoResultadosExist() {
        // Arrange
        when(resultadoRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Resultado> result = resultadoService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(resultadoRepository).findAll();
    }

    // ==================== findById Tests ====================

    @Test
    void findById_shouldReturnResultado_whenExists() {
        // Arrange
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(testResultado));

        // Act
        Resultado result = resultadoService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan Pérez", result.getPaciente());
        verify(resultadoRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        // Arrange
        when(resultadoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            resultadoService.findById(999L);
        });

        assertTrue(exception.getMessage().contains("Resultado no encontrado"));
        assertTrue(exception.getMessage().contains("999"));
        verify(resultadoRepository).findById(999L);
    }

    // ==================== create Tests ====================

    @Test
    void create_shouldSaveResultado_whenTipoAnalisisExists() {
        // Arrange
        Resultado newResultado = new Resultado();
        newResultado.setPaciente("Nuevo Paciente");
        newResultado.setFechaRealizacion(LocalDateTime.now());
        newResultado.setTipoAnalisis(testTipoAnalisis);
        newResultado.setLaboratorioId(100L);
        newResultado.setEstado("PENDIENTE");

        when(tipoAnalisisService.findById(1L)).thenReturn(testTipoAnalisis);
        when(resultadoRepository.save(any(Resultado.class))).thenReturn(newResultado);

        // Act
        Resultado result = resultadoService.create(newResultado);

        // Assert
        assertNotNull(result);
        assertEquals("Nuevo Paciente", result.getPaciente());
        verify(tipoAnalisisService).findById(1L);
        verify(resultadoRepository).save(newResultado);
    }

    @Test
    void create_shouldThrowException_whenTipoAnalisisIsNull() {
        // Arrange
        Resultado newResultado = new Resultado();
        newResultado.setPaciente("Test");
        newResultado.setTipoAnalisis(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            resultadoService.create(newResultado);
        });

        assertTrue(exception.getMessage().contains("El tipo de análisis es requerido"));
        verify(resultadoRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenTipoAnalisisIdIsNull() {
        // Arrange
        TipoAnalisis tipoSinId = new TipoAnalisis();
        tipoSinId.setId(null);

        Resultado newResultado = new Resultado();
        newResultado.setPaciente("Test");
        newResultado.setTipoAnalisis(tipoSinId);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            resultadoService.create(newResultado);
        });

        assertTrue(exception.getMessage().contains("El tipo de análisis es requerido"));
        verify(resultadoRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenTipoAnalisisNotFound() {
        // Arrange
        TipoAnalisis tipoNoExiste = new TipoAnalisis();
        tipoNoExiste.setId(999L);

        Resultado newResultado = new Resultado();
        newResultado.setPaciente("Test");
        newResultado.setTipoAnalisis(tipoNoExiste);

        when(tipoAnalisisService.findById(999L)).thenThrow(
                new IllegalArgumentException("Tipo de análisis no encontrado con ID: 999"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            resultadoService.create(newResultado);
        });

        assertTrue(exception.getMessage().contains("Tipo de análisis no encontrado"));
        verify(resultadoRepository, never()).save(any());
    }

    // ==================== update Tests ====================

    @Test
    void update_shouldUpdateResultado_whenExists() {
        // Arrange
        Resultado updateData = new Resultado();
        updateData.setPaciente("Paciente Actualizado");
        updateData.setFechaRealizacion(LocalDateTime.of(2025, 2, 1, 12, 0));
        updateData.setTipoAnalisis(testTipoAnalisis);
        updateData.setLaboratorioId(200L);
        updateData.setValorNumerico(new BigDecimal("95.00"));
        updateData.setValorTexto("Normal");
        updateData.setEstado("REVISADO");
        updateData.setObservaciones("Actualizado");

        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(testResultado));
        when(tipoAnalisisService.findById(1L)).thenReturn(testTipoAnalisis);
        when(resultadoRepository.save(any(Resultado.class))).thenReturn(testResultado);

        // Act
        Resultado result = resultadoService.update(1L, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("Paciente Actualizado", testResultado.getPaciente());
        assertEquals(200L, testResultado.getLaboratorioId());
        verify(resultadoRepository).findById(1L);
        verify(resultadoRepository).save(any(Resultado.class));
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        // Arrange
        Resultado updateData = new Resultado();
        updateData.setPaciente("Test");

        when(resultadoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            resultadoService.update(999L, updateData);
        });

        assertTrue(exception.getMessage().contains("Resultado no encontrado"));
        verify(resultadoRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateTipoAnalisis_whenProvided() {
        // Arrange
        TipoAnalisis newTipo = new TipoAnalisis();
        newTipo.setId(2L);
        newTipo.setNombre("Colesterol");

        Resultado updateData = new Resultado();
        updateData.setPaciente("Test");
        updateData.setFechaRealizacion(LocalDateTime.now());
        updateData.setTipoAnalisis(newTipo);
        updateData.setLaboratorioId(100L);
        updateData.setEstado("PENDIENTE");

        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(testResultado));
        when(tipoAnalisisService.findById(2L)).thenReturn(newTipo);
        when(resultadoRepository.save(any(Resultado.class))).thenReturn(testResultado);

        // Act
        Resultado result = resultadoService.update(1L, updateData);

        // Assert
        assertNotNull(result);
        verify(tipoAnalisisService).findById(2L);
    }

    @Test
    void update_shouldNotChangeTipoAnalisis_whenNotProvided() {
        // Arrange
        Resultado updateData = new Resultado();
        updateData.setPaciente("Test");
        updateData.setFechaRealizacion(LocalDateTime.now());
        updateData.setTipoAnalisis(null);
        updateData.setLaboratorioId(100L);
        updateData.setEstado("PENDIENTE");

        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(testResultado));
        when(resultadoRepository.save(any(Resultado.class))).thenReturn(testResultado);

        // Act
        Resultado result = resultadoService.update(1L, updateData);

        // Assert
        assertNotNull(result);
        verify(tipoAnalisisService, never()).findById(anyLong());
    }

    // ==================== delete Tests ====================

    @Test
    void delete_shouldDeleteResultado_whenExists() {
        // Arrange
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(testResultado));
        doNothing().when(resultadoRepository).delete(testResultado);

        // Act
        assertDoesNotThrow(() -> resultadoService.delete(1L));

        // Assert
        verify(resultadoRepository).findById(1L);
        verify(resultadoRepository).delete(testResultado);
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        // Arrange
        when(resultadoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            resultadoService.delete(999L);
        });

        assertTrue(exception.getMessage().contains("Resultado no encontrado"));
        verify(resultadoRepository).findById(999L);
        verify(resultadoRepository, never()).delete(any());
    }
}
