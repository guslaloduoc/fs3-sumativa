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
 * DTO de respuesta para usuarios
 * NO expone el passwordHash por seguridad
 * Utilizado en todos los endpoints que retornan información de usuarios
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private Set<RoleResponseDto> roles;
}
