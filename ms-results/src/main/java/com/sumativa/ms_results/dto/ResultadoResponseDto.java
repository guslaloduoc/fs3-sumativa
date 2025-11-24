package com.sumativa.ms_results.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para Resultado.
 * Incluye información del tipo de análisis embebida.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoResponseDto {

    private Long id;
    private String paciente;
    private LocalDateTime fechaRealizacion;
    private TipoAnalisisResponseDto tipoAnalisis;
    private Long laboratorioId;
    private BigDecimal valorNumerico;
    private String valorTexto;
    private String estado;
    private String observaciones;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
