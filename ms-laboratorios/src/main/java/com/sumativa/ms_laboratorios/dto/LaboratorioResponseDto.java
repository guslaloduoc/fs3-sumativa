package com.sumativa.ms_laboratorios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para laboratorios
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaboratorioResponseDto {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
}
