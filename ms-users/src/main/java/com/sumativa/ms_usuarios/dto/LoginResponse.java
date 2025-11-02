package com.sumativa.ms_usuarios.dto;

import com.sumativa.ms_usuarios.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para la respuesta de inicio de sesión exitoso
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private Long id;
    private String fullName;
    private String email;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private Set<Role> roles;
    private String message;

    public LoginResponse(Long id, String fullName, String email, Boolean enabled,
                         LocalDateTime createdAt, Set<Role> roles) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.roles = roles;
        this.message = "Inicio de sesión exitoso";
    }
}
