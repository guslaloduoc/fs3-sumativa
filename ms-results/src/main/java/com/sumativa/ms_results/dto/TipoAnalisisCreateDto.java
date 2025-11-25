package com.sumativa.ms_results.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para crear un nuevo TipoAnalisis.
 * Incluye validaciones de negocio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoAnalisisCreateDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(min = 3, max = 50, message = "La categoría debe tener entre 3 y 50 caracteres")
    private String categoria;

    @Size(max = 20, message = "La unidad de medida no puede exceder 20 caracteres")
    private String unidadMedida;

    private BigDecimal valorReferenciaMin;
    private BigDecimal valorReferenciaMax;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}
