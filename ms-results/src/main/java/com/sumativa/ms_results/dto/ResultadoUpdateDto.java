package com.sumativa.ms_results.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para actualizar un Resultado.
 * Todos los campos son opcionales (actualización parcial).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoUpdateDto {

    @Size(min = 3, max = 200, message = "El nombre del paciente debe tener entre 3 y 200 caracteres")
    private String paciente;

    private LocalDateTime fechaRealizacion;
    private Long tipoAnalisisId;
    private Long laboratorioId;
    private BigDecimal valorNumerico;

    @Size(max = 500, message = "El valor texto no puede exceder 500 caracteres")
    private String valorTexto;

    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    private String estado;

    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observaciones;
}
