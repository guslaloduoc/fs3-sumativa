#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para la respuesta de inicio de sesión exitoso
 * Utiliza RoleResponseDto en lugar de entidades Role
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
    private Set<RoleResponseDto> roles;
    private String message;

    public LoginResponse(Long id, String fullName, String email, Boolean enabled,
                         LocalDateTime createdAt, Set<RoleResponseDto> roles) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.roles = roles;
        this.message = "Inicio de sesión exitoso";
    }
}
