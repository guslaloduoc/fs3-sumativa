package com.sumativa.ms_laboratorios.service;

import com.sumativa.ms_laboratorios.entity.Asignacion;
import com.sumativa.ms_laboratorios.entity.Laboratorio;
import com.sumativa.ms_laboratorios.repository.AsignacionRepository;
import com.sumativa.ms_laboratorios.repository.LaboratorioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {

    @Mock
    private AsignacionRepository asignacionRepository;

    @Mock
    private LaboratorioRepository laboratorioRepository;

    @InjectMocks
    private AsignacionService asignacionService;

    private Asignacion testAsignacion;
    private Laboratorio testLaboratorio;

    @BeforeEach
    void setUp() {
        testLaboratorio = new Laboratorio();
        testLaboratorio.setId(1L);
        testLaboratorio.setNombre("Lab Test");
        testLaboratorio.setDireccion("Direccion Test");
        testLaboratorio.setTelefono("912345678");

        testAsignacion = new Asignacion();
        testAsignacion.setId(1L);
        testAsignacion.setPaciente("Paciente Test");
        testAsignacion.setFecha(LocalDate.of(2025, 1, 15));
        testAsignacion.setLaboratorio(testLaboratorio);
    }

    @Test
    void findAll_shouldReturnAllAsignaciones() {
        Asignacion asig2 = new Asignacion();
        asig2.setId(2L);
        asig2.setPaciente("Paciente 2");
        asig2.setFecha(LocalDate.now());
        asig2.setLaboratorio(testLaboratorio);

        when(asignacionRepository.findAll()).thenReturn(Arrays.asList(testAsignacion, asig2));

        List<Asignacion> result = asignacionService.findAll();

        assertEquals(2, result.size());
        verify(asignacionRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoAsignaciones() {
        when(asignacionRepository.findAll()).thenReturn(Arrays.asList());

        List<Asignacion> result = asignacionService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_shouldReturnAsignacion_whenExists() {
        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(testAsignacion));

        Optional<Asignacion> result = asignacionService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Paciente Test", result.get().getPaciente());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        when(asignacionRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Asignacion> result = asignacionService.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void create_shouldCreateAsignacion_whenValid() {
        Asignacion newAsig = new Asignacion();
        newAsig.setPaciente("Nuevo Paciente");
        newAsig.setFecha(LocalDate.of(2025, 2, 20));
        newAsig.setLaboratorio(testLaboratorio);

        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(asignacionRepository.save(any(Asignacion.class))).thenReturn(newAsig);

        Asignacion result = asignacionService.create(newAsig);

        assertNotNull(result);
        assertEquals("Nuevo Paciente", result.getPaciente());
        verify(asignacionRepository).save(any(Asignacion.class));
    }

    @Test
    void create_shouldThrowException_whenIdNotNull() {
        Asignacion newAsig = new Asignacion();
        newAsig.setId(1L);
        newAsig.setPaciente("Nuevo Paciente");

        assertThrows(IllegalArgumentException.class, () ->
            asignacionService.create(newAsig));

        verify(asignacionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenLaboratorioNotFound() {
        Laboratorio labNotFound = new Laboratorio();
        labNotFound.setId(999L);

        Asignacion newAsig = new Asignacion();
        newAsig.setPaciente("Nuevo Paciente");
        newAsig.setFecha(LocalDate.now());
        newAsig.setLaboratorio(labNotFound);

        when(laboratorioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            asignacionService.create(newAsig));
    }

    @Test
    void create_shouldCreateAsignacion_withNullLaboratorio() {
        Asignacion newAsig = new Asignacion();
        newAsig.setPaciente("Paciente Sin Lab");
        newAsig.setFecha(LocalDate.now());

        when(asignacionRepository.save(any(Asignacion.class))).thenReturn(newAsig);

        Asignacion result = asignacionService.create(newAsig);

        assertNotNull(result);
        verify(asignacionRepository).save(any(Asignacion.class));
    }

    @Test
    void create_shouldCreateAsignacion_withNullLaboratorioId() {
        Laboratorio labNullId = new Laboratorio();
        labNullId.setNombre("Lab sin ID");

        Asignacion newAsig = new Asignacion();
        newAsig.setPaciente("Paciente");
        newAsig.setFecha(LocalDate.now());
        newAsig.setLaboratorio(labNullId);

        when(asignacionRepository.save(any(Asignacion.class))).thenReturn(newAsig);

        Asignacion result = asignacionService.create(newAsig);

        assertNotNull(result);
    }

    @Test
    void update_shouldUpdateAsignacion_whenExists() {
        Asignacion updatedData = new Asignacion();
        updatedData.setPaciente("Paciente Actualizado");
        updatedData.setFecha(LocalDate.of(2025, 3, 10));
        updatedData.setLaboratorio(testLaboratorio);

        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(testAsignacion));
        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(asignacionRepository.save(any(Asignacion.class))).thenReturn(testAsignacion);

        Asignacion result = asignacionService.update(1L, updatedData);

        assertNotNull(result);
        verify(asignacionRepository).save(any(Asignacion.class));
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        when(asignacionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            asignacionService.update(999L, testAsignacion));
    }

    @Test
    void update_shouldThrowException_whenLaboratorioNotFound() {
        Laboratorio labNotFound = new Laboratorio();
        labNotFound.setId(999L);

        Asignacion updatedData = new Asignacion();
        updatedData.setPaciente("Paciente");
        updatedData.setFecha(LocalDate.now());
        updatedData.setLaboratorio(labNotFound);

        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(testAsignacion));
        when(laboratorioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            asignacionService.update(1L, updatedData));
    }

    @Test
    void update_shouldUpdateAsignacion_withNullLaboratorio() {
        Asignacion updatedData = new Asignacion();
        updatedData.setPaciente("Paciente Actualizado");
        updatedData.setFecha(LocalDate.now());

        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(testAsignacion));
        when(asignacionRepository.save(any(Asignacion.class))).thenReturn(testAsignacion);

        Asignacion result = asignacionService.update(1L, updatedData);

        assertNotNull(result);
    }

    @Test
    void update_shouldUpdateAsignacion_withNullLaboratorioId() {
        Laboratorio labNullId = new Laboratorio();
        labNullId.setNombre("Lab sin ID");

        Asignacion updatedData = new Asignacion();
        updatedData.setPaciente("Paciente Actualizado");
        updatedData.setFecha(LocalDate.now());
        updatedData.setLaboratorio(labNullId);

        when(asignacionRepository.findById(1L)).thenReturn(Optional.of(testAsignacion));
        when(asignacionRepository.save(any(Asignacion.class))).thenReturn(testAsignacion);

        Asignacion result = asignacionService.update(1L, updatedData);

        assertNotNull(result);
    }

    @Test
    void delete_shouldDeleteAsignacion_whenExists() {
        when(asignacionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(asignacionRepository).deleteById(1L);

        assertDoesNotThrow(() -> asignacionService.delete(1L));

        verify(asignacionRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        when(asignacionRepository.existsById(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
            asignacionService.delete(999L));

        verify(asignacionRepository, never()).deleteById(any());
    }
}
