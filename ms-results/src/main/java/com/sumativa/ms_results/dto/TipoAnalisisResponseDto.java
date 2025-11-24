package com.sumativa.ms_results.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta para TipoAnalisis.
 * Expone solo los campos necesarios al cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoAnalisisResponseDto {

    private Long id;
    private String nombre;
    private String categoria;
    private String unidadMedida;
    private BigDecimal valorReferenciaMin;
    private BigDecimal valorReferenciaMax;
    private Boolean activo;
}
