package com.sumativa.ms_results.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para actualizar un TipoAnalisis.
 * Todos los campos son opcionales (actualización parcial).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoAnalisisUpdateDto {

    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Size(min = 3, max = 50, message = "La categoría debe tener entre 3 y 50 caracteres")
    private String categoria;

    @Size(max = 20, message = "La unidad de medida no puede exceder 20 caracteres")
    private String unidadMedida;

    private BigDecimal valorReferenciaMin;
    private BigDecimal valorReferenciaMax;
    private Boolean activo;
}
