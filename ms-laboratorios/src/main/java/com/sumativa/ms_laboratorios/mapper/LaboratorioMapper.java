package com.sumativa.ms_laboratorios.mapper;

import com.sumativa.ms_laboratorios.dto.LaboratorioCreateDto;
import com.sumativa.ms_laboratorios.dto.LaboratorioResponseDto;
import com.sumativa.ms_laboratorios.dto.LaboratorioUpdateDto;
import com.sumativa.ms_laboratorios.entity.Laboratorio;

/**
 * Mapper para convertir entre entidades Laboratorio y sus DTOs
 */
public class LaboratorioMapper {

    private LaboratorioMapper() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Convierte una entidad Laboratorio a LaboratorioResponseDto
     */
    public static LaboratorioResponseDto toResponseDto(Laboratorio laboratorio) {
        if (laboratorio == null) {
            return null;
        }

        return new LaboratorioResponseDto(
            laboratorio.getId(),
            laboratorio.getNombre(),
            laboratorio.getDireccion(),
            laboratorio.getTelefono()
        );
    }

    /**
     * Convierte un LaboratorioCreateDto a entidad Laboratorio
     */
    public static Laboratorio toEntity(LaboratorioCreateDto createDto) {
        if (createDto == null) {
            return null;
        }

        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setNombre(createDto.getNombre());
        laboratorio.setDireccion(createDto.getDireccion());
        laboratorio.setTelefono(createDto.getTelefono());
        return laboratorio;
    }

    /**
     * Actualiza una entidad Laboratorio con los valores de LaboratorioUpdateDto
     * Solo actualiza los campos que no son null
     */
    public static void updateEntityFromDto(Laboratorio laboratorio, LaboratorioUpdateDto updateDto) {
        if (updateDto == null) {
            return;
        }

        if (updateDto.getNombre() != null) {
            laboratorio.setNombre(updateDto.getNombre());
        }
        if (updateDto.getDireccion() != null) {
            laboratorio.setDireccion(updateDto.getDireccion());
        }
        if (updateDto.getTelefono() != null) {
            laboratorio.setTelefono(updateDto.getTelefono());
        }
    }
}
