package com.sumativa.ms_results.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para crear un nuevo Resultado.
 * Incluye validaciones de negocio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoCreateDto {

    @NotBlank(message = "El nombre del paciente es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre del paciente debe tener entre 3 y 200 caracteres")
    private String paciente;

    @NotNull(message = "La fecha de realización es obligatoria")
    private LocalDateTime fechaRealizacion;

    @NotNull(message = "El tipo de análisis es obligatorio")
    private Long tipoAnalisisId;

    @NotNull(message = "El ID del laboratorio es obligatorio")
    private Long laboratorioId;

    private BigDecimal valorNumerico;

    @Size(max = 500, message = "El valor texto no puede exceder 500 caracteres")
    private String valorTexto;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    private String estado;

    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observaciones;
}
