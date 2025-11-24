package com.sumativa.ms_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para roles
 * Expone solo la información esencial del rol
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDto {
    private String name;
}
