package com.sumativa.ms_results.mapper;

import com.sumativa.ms_results.dto.TipoAnalisisCreateDto;
import com.sumativa.ms_results.dto.TipoAnalisisResponseDto;
import com.sumativa.ms_results.dto.TipoAnalisisUpdateDto;
import com.sumativa.ms_results.entity.TipoAnalisis;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre TipoAnalisis Entity y DTOs.
 * Centraliza la lógica de conversión.
 */
@Component
public class TipoAnalisisMapper {

    /**
     * Convierte Entity a DTO de respuesta.
     */
    public TipoAnalisisResponseDto toDto(TipoAnalisis entity) {
        if (entity == null) {
            return null;
        }

        return TipoAnalisisResponseDto.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .categoria(entity.getCategoria())
                .unidadMedida(entity.getUnidadMedida())
                .valorReferenciaMin(entity.getValorReferenciaMin())
                .valorReferenciaMax(entity.getValorReferenciaMax())
                .activo(entity.getActivo())
                .build();
    }

    /**
     * Convierte CreateDTO a Entity.
     */
    public TipoAnalisis toEntity(TipoAnalisisCreateDto dto) {
        if (dto == null) {
            return null;
        }

        TipoAnalisis entity = new TipoAnalisis();
        entity.setNombre(dto.getNombre());
        entity.setCategoria(dto.getCategoria());
        entity.setUnidadMedida(dto.getUnidadMedida());
        entity.setValorReferenciaMin(dto.getValorReferenciaMin());
        entity.setValorReferenciaMax(dto.getValorReferenciaMax());
        entity.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return entity;
    }

    /**
     * Actualiza una Entity existente con los datos del UpdateDTO.
     * Solo actualiza campos no nulos (actualización parcial).
     */
    public void updateEntityFromDto(TipoAnalisisUpdateDto dto, TipoAnalisis entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getNombre() != null) {
            entity.setNombre(dto.getNombre());
        }
        if (dto.getCategoria() != null) {
            entity.setCategoria(dto.getCategoria());
        }
        if (dto.getUnidadMedida() != null) {
            entity.setUnidadMedida(dto.getUnidadMedida());
        }
        if (dto.getValorReferenciaMin() != null) {
            entity.setValorReferenciaMin(dto.getValorReferenciaMin());
        }
        if (dto.getValorReferenciaMax() != null) {
            entity.setValorReferenciaMax(dto.getValorReferenciaMax());
        }
        if (dto.getActivo() != null) {
            entity.setActivo(dto.getActivo());
        }
    }
}
