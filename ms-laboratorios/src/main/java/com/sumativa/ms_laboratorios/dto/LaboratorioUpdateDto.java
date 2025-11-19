package com.sumativa.ms_laboratorios.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualización de laboratorios
 * Todos los campos son opcionales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaboratorioUpdateDto {

    @Size(max = 150, message = "El nombre no puede exceder los 150 caracteres")
    private String nombre;

    @Size(max = 250, message = "La dirección no puede exceder los 250 caracteres")
    private String direccion;

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String telefono;
}
