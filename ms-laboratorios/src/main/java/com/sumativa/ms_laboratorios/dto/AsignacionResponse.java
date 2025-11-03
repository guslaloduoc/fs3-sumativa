package com.sumativa.ms_laboratorios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para la respuesta de operaciones sobre asignación
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionResponse {

    private Long id;
    private String paciente;
    private LocalDate fecha;
    private LaboratorioResponse laboratorio;
}
