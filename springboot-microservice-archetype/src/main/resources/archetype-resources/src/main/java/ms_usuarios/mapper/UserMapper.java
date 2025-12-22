#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ms_usuarios.mapper;

import ${package}.ms_usuarios.dto.*;
import ${package}.ms_usuarios.entity.Role;
import ${package}.ms_usuarios.entity.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades User/Role y sus DTOs
 * Utiliza métodos estáticos para simplificar el uso
 */
public class UserMapper {

    private UserMapper() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Convierte una entidad User a UserResponseDto
     * NO incluye el passwordHash por seguridad
     */
    public static UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        Set<RoleResponseDto> roleDtos = user.getRoles().stream()
            .map(UserMapper::toRoleResponseDto)
            .collect(Collectors.toSet());

        return new UserResponseDto(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getEnabled(),
            user.getCreatedAt(),
            roleDtos
        );
    }

    /**
     * Convierte una entidad Role a RoleResponseDto
     */
    public static RoleResponseDto toRoleResponseDto(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleResponseDto(role.getName());
    }

    /**
     * Convierte un UserCreateDto a entidad User
     * Nota: El password se mapea directamente a passwordHash según requerimientos académicos
     */
    public static User toEntity(UserCreateDto createDto) {
        if (createDto == null) {
            return null;
        }

        User user = new User();
        user.setFullName(createDto.getFullName());
        user.setEmail(createDto.getEmail());
        user.setPasswordHash(createDto.getPassword());
        user.setEnabled(createDto.getEnabled() != null ? createDto.getEnabled() : true);
        user.setRoles(new HashSet<>());
        return user;
    }

    /**
     * Actualiza una entidad User con los valores de UserUpdateDto
     * Solo actualiza los campos que no son null
     */
    public static void updateEntityFromDto(User user, UserUpdateDto updateDto) {
        if (updateDto == null) {
            return;
        }

        if (updateDto.getFullName() != null) {
            user.setFullName(updateDto.getFullName());
        }
        if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }
        if (updateDto.getPassword() != null) {
            user.setPasswordHash(updateDto.getPassword());
        }
        if (updateDto.getEnabled() != null) {
            user.setEnabled(updateDto.getEnabled());
        }
    }

    /**
     * Convierte una entidad User a LoginResponse
     * Utilizado específicamente en el endpoint de login
     */
    public static LoginResponse toLoginResponse(User user) {
        if (user == null) {
            return null;
        }

        Set<RoleResponseDto> roleDtos = user.getRoles().stream()
            .map(UserMapper::toRoleResponseDto)
            .collect(Collectors.toSet());

        return new LoginResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getEnabled(),
            user.getCreatedAt(),
            roleDtos
        );
    }
}
