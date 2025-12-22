package com.sumativa.ms_laboratorios.mapper;

import com.sumativa.ms_laboratorios.dto.AsignacionCreateDto;
import com.sumativa.ms_laboratorios.dto.AsignacionResponseDto;
import com.sumativa.ms_laboratorios.dto.AsignacionUpdateDto;
import com.sumativa.ms_laboratorios.entity.Asignacion;
import com.sumativa.ms_laboratorios.entity.Laboratorio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AsignacionMapperTest {

    private Laboratorio createTestLaboratorio() {
        return new Laboratorio(1L, "Lab Test", "Direccion Test", "912345678");
    }

    @Test
    void toResponseDto_shouldConvertAsignacionToDto() {
        Laboratorio lab = createTestLaboratorio();
        Asignacion asignacion = new Asignacion(1L, "Paciente Test", LocalDate.of(2025, 1, 15), lab);

        AsignacionResponseDto result = AsignacionMapper.toResponseDto(asignacion);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Paciente Test", result.getPaciente());
        assertEquals(LocalDate.of(2025, 1, 15), result.getFecha());
        assertNotNull(result.getLaboratorio());
        assertEquals(1L, result.getLaboratorio().getId());
    }

    @Test
    void toResponseDto_shouldReturnNull_whenAsignacionIsNull() {
        AsignacionResponseDto result = AsignacionMapper.toResponseDto(null);

        assertNull(result);
    }

    @Test
    void toResponseDto_shouldHandleNullLaboratorio() {
        Asignacion asignacion = new Asignacion(1L, "Paciente Test", LocalDate.of(2025, 1, 15), null);

        AsignacionResponseDto result = AsignacionMapper.toResponseDto(asignacion);

        assertNotNull(result);
        assertNull(result.getLaboratorio());
    }

    @Test
    void toEntity_shouldConvertDtoToAsignacion() {
        Laboratorio lab = createTestLaboratorio();
        AsignacionCreateDto createDto = new AsignacionCreateDto();
        createDto.setPaciente("Nuevo Paciente");
        createDto.setFecha(LocalDate.of(2025, 2, 20));
        createDto.setLaboratorioId(1L);

        Asignacion result = AsignacionMapper.toEntity(createDto, lab);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Nuevo Paciente", result.getPaciente());
        assertEquals(LocalDate.of(2025, 2, 20), result.getFecha());
        assertEquals(lab, result.getLaboratorio());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        Asignacion result = AsignacionMapper.toEntity(null, createTestLaboratorio());

        assertNull(result);
    }

    @Test
    void toEntity_shouldSetNullLaboratorio_whenProvided() {
        AsignacionCreateDto createDto = new AsignacionCreateDto();
        createDto.setPaciente("Paciente");
        createDto.setFecha(LocalDate.now());

        Asignacion result = AsignacionMapper.toEntity(createDto, null);

        assertNotNull(result);
        assertNull(result.getLaboratorio());
    }

    @Test
    void updateEntityFromDto_shouldUpdateAllFields() {
        Laboratorio lab = createTestLaboratorio();
        Laboratorio newLab = new Laboratorio(2L, "Nuevo Lab", "Nueva Dir", "987654321");
        Asignacion asignacion = new Asignacion(1L, "Paciente Original", LocalDate.of(2025, 1, 15), lab);

        AsignacionUpdateDto updateDto = new AsignacionUpdateDto();
        updateDto.setPaciente("Paciente Actualizado");
        updateDto.setFecha(LocalDate.of(2025, 3, 10));

        AsignacionMapper.updateEntityFromDto(asignacion, updateDto, newLab);

        assertEquals("Paciente Actualizado", asignacion.getPaciente());
        assertEquals(LocalDate.of(2025, 3, 10), asignacion.getFecha());
        assertEquals(newLab, asignacion.getLaboratorio());
    }

    @Test
    void updateEntityFromDto_shouldNotUpdateNull_whenDtoIsNull() {
        Asignacion asignacion = new Asignacion(1L, "Paciente Original", LocalDate.of(2025, 1, 15), createTestLaboratorio());

        AsignacionMapper.updateEntityFromDto(asignacion, null, null);

        assertEquals("Paciente Original", asignacion.getPaciente());
    }

    @Test
    void updateEntityFromDto_shouldOnlyUpdateProvidedFields() {
        Laboratorio lab = createTestLaboratorio();
        Asignacion asignacion = new Asignacion(1L, "Paciente Original", LocalDate.of(2025, 1, 15), lab);

        AsignacionUpdateDto updateDto = new AsignacionUpdateDto();
        updateDto.setPaciente("Paciente Actualizado");
        // fecha is null

        AsignacionMapper.updateEntityFromDto(asignacion, updateDto, null);

        assertEquals("Paciente Actualizado", asignacion.getPaciente());
        assertEquals(LocalDate.of(2025, 1, 15), asignacion.getFecha());
        assertEquals(lab, asignacion.getLaboratorio()); // Not changed since newLab is null
    }

    @Test
    void updateEntityFromDto_shouldUpdateOnlyFecha() {
        Laboratorio lab = createTestLaboratorio();
        Asignacion asignacion = new Asignacion(1L, "Paciente Original", LocalDate.of(2025, 1, 15), lab);

        AsignacionUpdateDto updateDto = new AsignacionUpdateDto();
        updateDto.setFecha(LocalDate.of(2025, 6, 20));

        AsignacionMapper.updateEntityFromDto(asignacion, updateDto, null);

        assertEquals("Paciente Original", asignacion.getPaciente());
        assertEquals(LocalDate.of(2025, 6, 20), asignacion.getFecha());
    }

    @Test
    void updateEntityFromDto_shouldUpdateOnlyLaboratorio() {
        Laboratorio lab = createTestLaboratorio();
        Laboratorio newLab = new Laboratorio(2L, "Nuevo Lab", "Dir", "123");
        Asignacion asignacion = new Asignacion(1L, "Paciente Original", LocalDate.of(2025, 1, 15), lab);

        AsignacionUpdateDto updateDto = new AsignacionUpdateDto();
        // All fields null

        AsignacionMapper.updateEntityFromDto(asignacion, updateDto, newLab);

        assertEquals("Paciente Original", asignacion.getPaciente());
        assertEquals(LocalDate.of(2025, 1, 15), asignacion.getFecha());
        assertEquals(newLab, asignacion.getLaboratorio());
    }
}
