package com.sumativa.ms_laboratorios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de operaciones sobre laboratorio
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaboratorioResponse {

    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
}
