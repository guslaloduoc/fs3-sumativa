#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ms_usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualización de usuarios
 * Todos los campos son opcionales (se actualizan solo los no-null)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @Size(max = 150, message = "El nombre completo no puede exceder 150 caracteres")
    private String fullName;

    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Size(min = 4, max = 200, message = "La contraseña debe tener entre 4 y 200 caracteres")
    private String password;

    private Boolean enabled;
}
