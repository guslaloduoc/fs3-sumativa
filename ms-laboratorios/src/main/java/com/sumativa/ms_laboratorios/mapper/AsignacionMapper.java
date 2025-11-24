package com.sumativa.ms_laboratorios.mapper;

import com.sumativa.ms_laboratorios.dto.AsignacionCreateDto;
import com.sumativa.ms_laboratorios.dto.AsignacionResponseDto;
import com.sumativa.ms_laboratorios.dto.AsignacionUpdateDto;
import com.sumativa.ms_laboratorios.entity.Asignacion;
import com.sumativa.ms_laboratorios.entity.Laboratorio;

/**
 * Mapper para convertir entre entidades Asignacion y sus DTOs
 */
public class AsignacionMapper {

    private AsignacionMapper() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Convierte una entidad Asignacion a AsignacionResponseDto
     */
    public static AsignacionResponseDto toResponseDto(Asignacion asignacion) {
        if (asignacion == null) {
            return null;
        }

        return new AsignacionResponseDto(
            asignacion.getId(),
            asignacion.getPaciente(),
            asignacion.getFecha(),
            LaboratorioMapper.toResponseDto(asignacion.getLaboratorio())
        );
    }

    /**
     * Convierte un AsignacionCreateDto a entidad Asignacion
     * Nota: El Laboratorio debe ser asignado por el servicio después de recuperarlo de la BD
     */
    public static Asignacion toEntity(AsignacionCreateDto createDto, Laboratorio laboratorio) {
        if (createDto == null) {
            return null;
        }

        Asignacion asignacion = new Asignacion();
        asignacion.setPaciente(createDto.getPaciente());
        asignacion.setFecha(createDto.getFecha());
        asignacion.setLaboratorio(laboratorio);
        return asignacion;
    }

    /**
     * Actualiza una entidad Asignacion con los valores de AsignacionUpdateDto
     * Solo actualiza los campos que no son null
     * Nota: El Laboratorio debe ser actualizado por el servicio si laboratorioId no es null
     */
    public static void updateEntityFromDto(Asignacion asignacion, AsignacionUpdateDto updateDto, Laboratorio laboratorio) {
        if (updateDto == null) {
            return;
        }

        if (updateDto.getPaciente() != null) {
            asignacion.setPaciente(updateDto.getPaciente());
        }
        if (updateDto.getFecha() != null) {
            asignacion.setFecha(updateDto.getFecha());
        }
        if (laboratorio != null) {
            asignacion.setLaboratorio(laboratorio);
        }
    }
}
