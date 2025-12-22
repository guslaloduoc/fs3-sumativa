#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para creación de usuarios
 * Define los campos necesarios al crear un nuevo usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre completo no puede exceder 150 caracteres")
    private String fullName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max = 200, message = "La contraseña debe tener entre 4 y 200 caracteres")
    private String password;

    private Boolean enabled = true;
}
