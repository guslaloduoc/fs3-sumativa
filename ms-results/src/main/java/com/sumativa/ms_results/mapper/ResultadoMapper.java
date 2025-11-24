package com.sumativa.ms_results.mapper;

import com.sumativa.ms_results.dto.ResultadoCreateDto;
import com.sumativa.ms_results.dto.ResultadoResponseDto;
import com.sumativa.ms_results.dto.ResultadoUpdateDto;
import com.sumativa.ms_results.entity.Resultado;
import com.sumativa.ms_results.entity.TipoAnalisis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Resultado Entity y DTOs.
 * Centraliza la lógica de conversión.
 */
@Component
@RequiredArgsConstructor
public class ResultadoMapper {

    private final TipoAnalisisMapper tipoAnalisisMapper;

    /**
     * Convierte Entity a DTO de respuesta.
     * Incluye el TipoAnalisis embebido.
     */
    public ResultadoResponseDto toDto(Resultado entity) {
        if (entity == null) {
            return null;
        }

        return ResultadoResponseDto.builder()
                .id(entity.getId())
                .paciente(entity.getPaciente())
                .fechaRealizacion(entity.getFechaRealizacion())
                .tipoAnalisis(tipoAnalisisMapper.toDto(entity.getTipoAnalisis()))
                .laboratorioId(entity.getLaboratorioId())
                .valorNumerico(entity.getValorNumerico())
                .valorTexto(entity.getValorTexto())
                .estado(entity.getEstado())
                .observaciones(entity.getObservaciones())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }

    /**
     * Convierte CreateDTO a Entity.
     * Requiere que se setee el TipoAnalisis externamente.
     */
    public Resultado toEntity(ResultadoCreateDto dto, TipoAnalisis tipoAnalisis) {
        if (dto == null) {
            return null;
        }

        Resultado entity = new Resultado();
        entity.setPaciente(dto.getPaciente());
        entity.setFechaRealizacion(dto.getFechaRealizacion());
        entity.setTipoAnalisis(tipoAnalisis);
        entity.setLaboratorioId(dto.getLaboratorioId());
        entity.setValorNumerico(dto.getValorNumerico());
        entity.setValorTexto(dto.getValorTexto());
        entity.setEstado(dto.getEstado() != null ? dto.getEstado() : "PENDIENTE");
        entity.setObservaciones(dto.getObservaciones());
        return entity;
    }

    /**
     * Actualiza una Entity existente con los datos del UpdateDTO.
     * Solo actualiza campos no nulos (actualización parcial).
     */
    public void updateEntityFromDto(ResultadoUpdateDto dto, Resultado entity, TipoAnalisis tipoAnalisis) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getPaciente() != null) {
            entity.setPaciente(dto.getPaciente());
        }
        if (dto.getFechaRealizacion() != null) {
            entity.setFechaRealizacion(dto.getFechaRealizacion());
        }
        if (tipoAnalisis != null) {
            entity.setTipoAnalisis(tipoAnalisis);
        }
        if (dto.getLaboratorioId() != null) {
            entity.setLaboratorioId(dto.getLaboratorioId());
        }
        if (dto.getValorNumerico() != null) {
            entity.setValorNumerico(dto.getValorNumerico());
        }
        if (dto.getValorTexto() != null) {
            entity.setValorTexto(dto.getValorTexto());
        }
        if (dto.getEstado() != null) {
            entity.setEstado(dto.getEstado());
        }
        if (dto.getObservaciones() != null) {
            entity.setObservaciones(dto.getObservaciones());
        }
    }
}
