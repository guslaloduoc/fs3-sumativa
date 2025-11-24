package com.sumativa.ms_laboratorios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para creación de asignaciones
 * Recibe el ID del laboratorio en lugar de la entidad completa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionCreateDto {

    @NotBlank(message = "El nombre del paciente es obligatorio")
    @Size(max = 200, message = "El nombre del paciente no puede exceder los 200 caracteres")
    private String paciente;

    @NotNull(message = "La fecha de asignación es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El ID del laboratorio es obligatorio")
    private Long laboratorioId;
}
