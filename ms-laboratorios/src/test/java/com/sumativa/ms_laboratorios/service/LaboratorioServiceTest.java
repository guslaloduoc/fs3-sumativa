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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LaboratorioServiceTest {

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
        testLaboratorio.setNombre("Laboratorio Test");
        testLaboratorio.setDireccion("Direccion Test");
        testLaboratorio.setTelefono("912345678");
    }

    @Test
    void findAll_shouldReturnAllLaboratorios() {
        Laboratorio lab2 = new Laboratorio();
        lab2.setId(2L);
        lab2.setNombre("Lab 2");

        when(laboratorioRepository.findAll()).thenReturn(Arrays.asList(testLaboratorio, lab2));

        List<Laboratorio> result = laboratorioService.findAll();

        assertEquals(2, result.size());
        verify(laboratorioRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoLaboratorios() {
        when(laboratorioRepository.findAll()).thenReturn(Arrays.asList());

        List<Laboratorio> result = laboratorioService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_shouldReturnLaboratorio_whenExists() {
        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(testLaboratorio));

        Optional<Laboratorio> result = laboratorioService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Laboratorio Test", result.get().getNombre());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        when(laboratorioRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Laboratorio> result = laboratorioService.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void create_shouldCreateLaboratorio_whenValid() {
        Laboratorio newLab = new Laboratorio();
        newLab.setNombre("Nuevo Lab");
        newLab.setDireccion("Nueva Direccion");
        newLab.setTelefono("987654321");

        when(laboratorioRepository.findByNombreIgnoreCase("Nuevo Lab")).thenReturn(Optional.empty());
        when(laboratorioRepository.save(any(Laboratorio.class))).thenReturn(newLab);

        Laboratorio result = laboratorioService.create(newLab);

        assertNotNull(result);
        assertEquals("Nuevo Lab", result.getNombre());
        verify(laboratorioRepository).save(any(Laboratorio.class));
    }

    @Test
    void create_shouldThrowException_whenIdNotNull() {
        Laboratorio newLab = new Laboratorio();
        newLab.setId(1L);
        newLab.setNombre("Nuevo Lab");

        assertThrows(IllegalArgumentException.class, () ->
            laboratorioService.create(newLab));

        verify(laboratorioRepository, never()).save(any());
    }

    @Test
    void create_shouldCreateLaboratorio_withoutPhone() {
        Laboratorio newLab = new Laboratorio();
        newLab.setNombre("Lab Sin Telefono");
        newLab.setDireccion("Direccion");

        when(laboratorioRepository.findByNombreIgnoreCase("Lab Sin Telefono")).thenReturn(Optional.empty());
        when(laboratorioRepository.save(any(Laboratorio.class))).thenReturn(newLab);

        Laboratorio result = laboratorioService.create(newLab);

        assertNotNull(result);
        verify(laboratorioRepository).save(any(Laboratorio.class));
    }

    @Test
    void create_shouldCreateLaboratorio_withEmptyPhone() {
        Laboratorio newLab = new Laboratorio();
        newLab.setNombre("Lab Telefono Vacio");
        newLab.setDireccion("Direccion");
        newLab.setTelefono("");

        when(laboratorioRepository.findByNombreIgnoreCase("Lab Telefono Vacio")).thenReturn(Optional.empty());
        when(laboratorioRepository.save(any(Laboratorio.class))).thenReturn(newLab);

        Laboratorio result = laboratorioService.create(newLab);

        assertNotNull(result);
        verify(laboratorioRepository).save(any(Laboratorio.class));
    }

    @Test
    void create_shouldThrowException_whenPhoneTooLong() {
        Laboratorio newLab = new Laboratorio();
        newLab.setNombre("Lab Test");
        newLab.setTelefono("1234567890123456789"); // 19 digits - too long

        when(laboratorioRepository.findByNombreIgnoreCase("Lab Test")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            laboratorioService.create(newLab));
    }

    @Test
    void update_shouldUpdateLaboratorio_whenExists() {
        Laboratorio updatedData = new Laboratorio();
        updatedData.setNombre("Lab Actualizado");
        updatedData.setDireccion("Nueva Direccion");
        updatedData.setTelefono("123456789");

        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(laboratorioRepository.findByNombreIgnoreCase("Lab Actualizado")).thenReturn(Optional.empty());
        when(laboratorioRepository.save(any(Laboratorio.class))).thenReturn(testLaboratorio);

        Laboratorio result = laboratorioService.update(1L, updatedData);

        assertNotNull(result);
        verify(laboratorioRepository).save(any(Laboratorio.class));
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        when(laboratorioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            laboratorioService.update(999L, testLaboratorio));
    }

    @Test
    void update_shouldThrowException_whenDuplicateName() {
        Laboratorio existingOther = new Laboratorio();
        existingOther.setId(2L);
        existingOther.setNombre("Otro Lab");

        Laboratorio updatedData = new Laboratorio();
        updatedData.setNombre("Otro Lab");
        updatedData.setDireccion("Direccion");
        updatedData.setTelefono("912345678");

        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(laboratorioRepository.findByNombreIgnoreCase("Otro Lab")).thenReturn(Optional.of(existingOther));

        assertThrows(IllegalArgumentException.class, () ->
            laboratorioService.update(1L, updatedData));
    }

    @Test
    void update_shouldUpdateLaboratorio_withoutPhone() {
        Laboratorio updatedData = new Laboratorio();
        updatedData.setNombre("Laboratorio Test");
        updatedData.setDireccion("Nueva Direccion");

        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(laboratorioRepository.save(any(Laboratorio.class))).thenReturn(testLaboratorio);

        Laboratorio result = laboratorioService.update(1L, updatedData);

        assertNotNull(result);
    }

    @Test
    void update_shouldUpdateLaboratorio_withEmptyPhone() {
        Laboratorio updatedData = new Laboratorio();
        updatedData.setNombre("Laboratorio Test");
        updatedData.setDireccion("Nueva Direccion");
        updatedData.setTelefono("");

        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(laboratorioRepository.save(any(Laboratorio.class))).thenReturn(testLaboratorio);

        Laboratorio result = laboratorioService.update(1L, updatedData);

        assertNotNull(result);
    }

    @Test
    void delete_shouldDeleteLaboratorio_whenNoAsignaciones() {
        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(asignacionRepository.countByLaboratorioId(1L)).thenReturn(0L);
        doNothing().when(laboratorioRepository).deleteById(1L);

        assertDoesNotThrow(() -> laboratorioService.delete(1L));

        verify(laboratorioRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        when(laboratorioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            laboratorioService.delete(999L));
    }

    @Test
    void delete_shouldThrowException_whenHasAsignaciones() {
        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(testLaboratorio));
        when(asignacionRepository.countByLaboratorioId(1L)).thenReturn(3L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            laboratorioService.delete(1L));

        assertTrue(exception.getMessage().contains("3 asignaciones activas"));
        verify(laboratorioRepository, never()).deleteById(any());
    }
}
