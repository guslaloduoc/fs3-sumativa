package com.sumativa.ms_laboratorios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de respuesta para asignaciones
 * Incluye información básica del laboratorio asociado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionResponseDto {
    private Long id;
    private String paciente;
    private LocalDate fecha;
    private LaboratorioResponseDto laboratorio;
}
