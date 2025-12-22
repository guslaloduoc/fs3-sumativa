#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ms_usuarios.service;

import ${package}.ms_usuarios.entity.Role;
import ${package}.ms_usuarios.entity.User;
import ${package}.ms_usuarios.repository.RoleRepository;
import ${package}.ms_usuarios.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para validaciones de negocio en UserService
 */
@ExtendWith(MockitoExtension.class)
class UserServiceValidationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("password123");
        testUser.setEnabled(true);
    }

    @Test
    void createUser_shouldThrowException_whenEmailDomainIsNotAuthorized() {
        // Arrange
        User user = new User();
        user.setEmail("test@gmail.com"); // Dominio NO autorizado

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertTrue(exception.getMessage().contains("Dominio de email no autorizado"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldSucceed_whenEmailDomainIsDuocuc() {
        // Arrange
        User user = new User();
        user.setEmail("test@duocuc.cl");
        user.setFullName("Test User");
        user.setPasswordHash("password123");

        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.createUser(user);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldSucceed_whenEmailDomainIsExample() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPasswordHash("password123");

        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.createUser(user);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_shouldThrowException_whenUserHasAdminRole() {
        // Arrange
        Role adminRole = new Role(1L, "ADMIN");
        testUser.setRoles(Set.of(adminRole));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(1L);
        });

        assertTrue(exception.getMessage().contains("No se puede eliminar un usuario con rol ADMIN"));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deleteUser_shouldSucceed_whenUserDoesNotHaveAdminRole() {
        // Arrange
        Role userRole = new Role(2L, "USER");
        testUser.setRoles(Set.of(userRole));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> userService.deleteUser(1L));

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    void toggleUserEnabled_shouldThrowException_whenDisablingPrimaryAdmin() {
        // Arrange
        testUser.setEmail("admin@example.com");
        testUser.setEnabled(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.toggleUserEnabled(1L);
        });

        assertTrue(exception.getMessage().contains("No se puede deshabilitar el usuario ADMIN principal"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void toggleUserEnabled_shouldSucceed_whenEnablingPrimaryAdmin() {
        // Arrange
        testUser.setEmail("admin@example.com");
        testUser.setEnabled(false); // Currently disabled

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.toggleUserEnabled(1L);

        // Assert
        assertTrue(result.getEnabled());
        verify(userRepository).save(any(User.class));
    }
}
