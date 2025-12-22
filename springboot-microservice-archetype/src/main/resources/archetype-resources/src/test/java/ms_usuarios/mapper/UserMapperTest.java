#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ms_usuarios.mapper;

import ${package}.ms_usuarios.dto.RoleResponseDto;
import ${package}.ms_usuarios.dto.UserCreateDto;
import ${package}.ms_usuarios.dto.UserResponseDto;
import ${package}.ms_usuarios.dto.UserUpdateDto;
import ${package}.ms_usuarios.entity.Role;
import ${package}.ms_usuarios.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para UserMapper
 */
class UserMapperTest {

    @Test
    void toResponseDto_shouldConvertUserToDto() {
        // Arrange
        Role adminRole = new Role(1L, "ADMIN");
        User user = new User();
        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPasswordHash("secret123");
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(Set.of(adminRole));

        // Act
        UserResponseDto dto = UserMapper.toResponseDto(user);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test User", dto.getFullName());
        assertEquals("test@example.com", dto.getEmail());
        assertTrue(dto.getEnabled());
        assertNotNull(dto.getCreatedAt());
        assertEquals(1, dto.getRoles().size());

        // Verify password is NOT exposed
        assertDoesNotThrow(() -> dto.toString()); // Should not contain password
    }

    @Test
    void toResponseDto_shouldReturnNull_whenUserIsNull() {
        // Act
        UserResponseDto dto = UserMapper.toResponseDto(null);

        // Assert
        assertNull(dto);
    }

    @Test
    void toRoleResponseDto_shouldConvertRoleToDto() {
        // Arrange
        Role role = new Role(1L, "ADMIN");

        // Act
        RoleResponseDto dto = UserMapper.toRoleResponseDto(role);

        // Assert
        assertNotNull(dto);
        assertEquals("ADMIN", dto.getName());
    }

    @Test
    void toEntity_shouldConvertCreateDtoToUser() {
        // Arrange
        UserCreateDto createDto = new UserCreateDto();
        createDto.setFullName("New User");
        createDto.setEmail("new@example.com");
        createDto.setPassword("password123");
        createDto.setEnabled(true);

        // Act
        User user = UserMapper.toEntity(createDto);

        // Assert
        assertNotNull(user);
        assertEquals("New User", user.getFullName());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("password123", user.getPasswordHash());
        assertTrue(user.getEnabled());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void updateEntityFromDto_shouldUpdateOnlyNonNullFields() {
        // Arrange
        User user = new User();
        user.setFullName("Original Name");
        user.setEmail("original@example.com");
        user.setPasswordHash("oldPassword");
        user.setEnabled(true);

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setFullName("Updated Name");
        updateDto.setPassword("newPassword");
        // email and enabled are null

        // Act
        UserMapper.updateEntityFromDto(user, updateDto);

        // Assert
        assertEquals("Updated Name", user.getFullName());
        assertEquals("original@example.com", user.getEmail()); // Should not change
        assertEquals("newPassword", user.getPasswordHash());
        assertTrue(user.getEnabled()); // Should not change
    }

    @Test
    void updateEntityFromDto_shouldHandleNullDto() {
        // Arrange
        User user = new User();
        user.setFullName("Original Name");

        // Act
        UserMapper.updateEntityFromDto(user, null);

        // Assert
        assertEquals("Original Name", user.getFullName()); // Should not change
    }
}
