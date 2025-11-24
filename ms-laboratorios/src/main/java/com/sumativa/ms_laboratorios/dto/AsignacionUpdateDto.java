package com.sumativa.ms_laboratorios.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para actualización de asignaciones
 * Todos los campos son opcionales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionUpdateDto {

    @Size(max = 200, message = "El nombre del paciente no puede exceder los 200 caracteres")
    private String paciente;

    private LocalDate fecha;

    private Long laboratorioId;
}
