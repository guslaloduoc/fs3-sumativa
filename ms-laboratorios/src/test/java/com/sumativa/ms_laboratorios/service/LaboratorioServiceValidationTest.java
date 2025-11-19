package com.sumativa.ms_laboratorios.service;

import com.sumativa.ms_laboratorios.entity.Laboratorio;
import com.sumativa.ms_laboratorios.repository.AsignacionRepository;
import com.sumativa.ms_laboratorios.repository.LaboratorioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests para validaciones de negocio en LaboratorioService
 */
@ExtendWith(MockitoExtension.class)
class LaboratorioServiceValidationTest {

    @Mock
    private LaboratorioRepository laboratorioRepository;

    @Mock
    private AsignacionRepository asignacionRepository;

    @InjectMocks
    private LaboratorioService laboratorioService;

    private Laboratorio testLaboratorio;

    @BeforeEach
    void setUp() {
        testLaboratorio = new Laboratorio();
        testLaboratorio.setId(1L);
        testLaboratorio.setNombre("Lab Test");
        testLaboratorio.setDireccion("Test Address");
        testLaboratorio.setTelefono("912345678");
    }

    @Test
    void create_shouldThrowException_whenNombreAlreadyExists() {
        // Arrange
        Laboratorio newLab = new Laboratorio();
        newLab.setNombre("Existing Lab");

        when(laboratorioRepository.findByNombreIgnoreCase("Existing Lab"))
                .thenReturn(Optional.of(testLaboratorio));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            laboratorioService.create(newLab);
        });

        assertTrue(exception.getMessage().contains("Ya existe un laboratorio con el nombre"));
        verify(laboratorioRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenPhoneFormatIsInvalid() {
        // Arrange
        Laboratorio newLab = new Laboratorio();
        newLab.setNombre("New Lab");
        newLab.setTelefono("12345"); // Too short

        when(laboratorioRepository.findByNombreIgnoreCase(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            laboratorioService.create(newLab);
        });

        assertTrue(exception.getMessage().contains("Formato de teléfono inválido"));
        verify(laboratorioRepository, never()).save(any());
    }

    @Test
    void create_shouldSucceed_whenPhoneFormatIsValid() {
        // Arrange
        Laboratorio newLab = new Laboratorio();
        newLab.setNombre("New Lab");
        newLab.setTelefono("912-345-678"); // Valid format with dashes

        when(laboratorioRepository.findByNombreIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(laboratorioRepository.save(any(Laboratorio.class))).thenReturn(newLab);

        // Act
        Laboratorio result = laboratorioService.create(newLab);

        // Assert
        assertNotNull(result);
        verify(laboratorioRepository).save(any(Laboratorio.class));
    }

    @Test
    void create_shouldAcceptPhoneWithSpacesAndParentheses() {
        // Arrange
        Laboratorio newLab = new Laboratorio();
        newLab.setNombre("New Lab");
        newLab.setTelefono("(91) 234-5678"); // Valid format with spaces and parentheses

        when(laboratorioRepository.findByNombreIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(laboratorioRepository.save(any(Laboratorio.class))).thenReturn(newLab);

        // Act
        Laboratorio result = laboratorioService.create(newLab);

        // Assert
        assertNotNull(result);
        verify(laboratorioRepository).save(any(Laboratorio.class));
    }

    @Test
    void delete_shouldThrowException_whenLaboratorioHasActiveAsignaciones() {
        // Arrange
        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(asignacionRepository.countByLaboratorioId(1L)).thenReturn(5L);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            laboratorioService.delete(1L);
        });

        assertTrue(exception.getMessage().contains("tiene 5 asignaciones activas"));
        verify(laboratorioRepository, never()).deleteById(any());
    }

    @Test
    void delete_shouldSucceed_whenLaboratorioHasNoAsignaciones() {
        // Arrange
        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(asignacionRepository.countByLaboratorioId(1L)).thenReturn(0L);
        doNothing().when(laboratorioRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> laboratorioService.delete(1L));

        // Assert
        verify(laboratorioRepository).deleteById(1L);
    }

    @Test
    void update_shouldAllowSameNombre_whenUpdatingOwnRecord() {
        // Arrange
        Laboratorio updatedData = new Laboratorio();
        updatedData.setNombre("Lab Test"); // Same name
        updatedData.setDireccion("New Address");
        updatedData.setTelefono("987654321");

        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(laboratorioRepository.save(any(Laboratorio.class))).thenReturn(testLaboratorio);

        // Act
        Laboratorio result = laboratorioService.update(1L, updatedData);

        // Assert
        assertNotNull(result);
        verify(laboratorioRepository).save(any(Laboratorio.class));
    }
}
