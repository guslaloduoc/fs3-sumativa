package com.sumativa.ms_usuarios.service;

import com.sumativa.ms_usuarios.dto.LoginResponse;
import com.sumativa.ms_usuarios.entity.Role;
import com.sumativa.ms_usuarios.entity.User;
import com.sumativa.ms_usuarios.repository.RoleRepository;
import com.sumativa.ms_usuarios.repository.UserRepository;
import com.sumativa.ms_usuarios.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role adminRole;
    private Role doctorRole;

    @BeforeEach
    void setUp() {
        adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ADMIN");

        doctorRole = new Role();
        doctorRole.setId(2L);
        doctorRole.setName("DOCTOR");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("password123");
        testUser.setEnabled(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setRoles(new HashSet<>());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<User> result = userService.findAll();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_shouldReturnUser() {
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenExists() {
        when(userRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertTrue(result);
    }

    @Test
    void createUser_shouldCreateNewUser() {
        User newUser = new User();
        newUser.setFullName("New User");
        newUser.setEmail("new@example.com");
        newUser.setPasswordHash("password");

        when(userRepository.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.createUser(newUser);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowException_whenEmailExists() {
        when(userRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
            userService.createUser(testUser));
    }

    @Test
    void createUser_shouldThrowException_whenInvalidDomain() {
        User newUser = new User();
        newUser.setEmail("test@gmail.com");

        when(userRepository.existsByEmailIgnoreCase("test@gmail.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
            userService.createUser(newUser));
    }

    @Test
    void createUser_shouldSetDefaultEnabled() {
        User newUser = new User();
        newUser.setEmail("new@duocuc.cl");
        newUser.setEnabled(null);

        when(userRepository.existsByEmailIgnoreCase("new@duocuc.cl")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.createUser(newUser);

        assertTrue(result.getEnabled());
    }

    @Test
    void createUser_shouldThrowException_whenRoleNotFound() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        HashSet<Role> roles = new HashSet<>();
        Role unknownRole = new Role();
        unknownRole.setName("UNKNOWN");
        roles.add(unknownRole);
        newUser.setRoles(roles);

        when(userRepository.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
        when(roleRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            userService.createUser(newUser));
    }

    @Test
    void updateUser_shouldUpdateFields() {
        User updatedDetails = new User();
        updatedDetails.setFullName("Updated Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(1L, updatedDetails);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowException_whenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            userService.updateUser(999L, testUser));
    }

    @Test
    void updateUser_shouldUpdateEmail_whenNew() {
        User updatedDetails = new User();
        updatedDetails.setEmail("newemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailIgnoreCase("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(1L, updatedDetails);

        assertNotNull(result);
    }

    @Test
    void updateUser_shouldThrowException_whenEmailExists() {
        User updatedDetails = new User();
        updatedDetails.setEmail("existing@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailIgnoreCase("existing@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
            userService.updateUser(1L, updatedDetails));
    }

    @Test
    void updateUser_shouldUpdatePassword() {
        User updatedDetails = new User();
        updatedDetails.setPasswordHash("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, updatedDetails);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateEnabled() {
        User updatedDetails = new User();
        updatedDetails.setEnabled(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, updatedDetails);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateRoles() {
        User updatedDetails = new User();
        updatedDetails.setRoles(new HashSet<>(Set.of(doctorRole)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("DOCTOR")).thenReturn(Optional.of(doctorRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, updatedDetails);

        verify(roleRepository).findByName("DOCTOR");
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowException_whenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            userService.deleteUser(999L));
    }

    @Test
    void deleteUser_shouldThrowException_whenAdminRole() {
        testUser.getRoles().add(adminRole);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () ->
            userService.deleteUser(1L));
    }

    @Test
    void toggleUserEnabled_shouldToggle() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.toggleUserEnabled(1L);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void toggleUserEnabled_shouldThrowException_whenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            userService.toggleUserEnabled(999L));
    }

    @Test
    void toggleUserEnabled_shouldThrowException_forPrimaryAdmin() {
        testUser.setEmail("admin@example.com");
        testUser.setEnabled(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () ->
            userService.toggleUserEnabled(1L));
    }

    @Test
    void assignRole_shouldAssignRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("DOCTOR")).thenReturn(Optional.of(doctorRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.assignRole(1L, "DOCTOR");

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void assignRole_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            userService.assignRole(999L, "DOCTOR"));
    }

    @Test
    void assignRole_shouldThrowException_whenRoleNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("INVALID")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            userService.assignRole(1L, "INVALID"));
    }

    @Test
    void removeRole_shouldRemoveRole() {
        testUser.getRoles().add(adminRole);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.removeRole(1L, "ADMIN");

        assertNotNull(result);
    }

    @Test
    void removeRole_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            userService.removeRole(999L, "ADMIN"));
    }

    @Test
    void removeRole_shouldThrowException_whenRoleNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("INVALID")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            userService.removeRole(1L, "INVALID"));
    }

    @Test
    void login_shouldReturnLoginResponse() {
        testUser.getRoles().add(adminRole);
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(anyString(), anyList())).thenReturn("jwt.token.here");

        LoginResponse result = userService.login("test@example.com", "password123");

        assertNotNull(result);
        assertEquals("jwt.token.here", result.getToken());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmailIgnoreCase("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            userService.login("notfound@example.com", "password"));
    }

    @Test
    void login_shouldThrowException_whenInvalidPassword() {
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () ->
            userService.login("test@example.com", "wrongpassword"));
    }

    @Test
    void login_shouldThrowException_whenUserDisabled() {
        testUser.setEnabled(false);
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () ->
            userService.login("test@example.com", "password123"));
    }

    @Test
    void createUser_shouldThrowException_whenEmailNull() {
        User newUser = new User();
        newUser.setEmail(null);

        when(userRepository.existsByEmailIgnoreCase(null)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
            userService.createUser(newUser));
    }

    @Test
    void createUser_shouldThrowException_whenEmailNoAtSymbol() {
        User newUser = new User();
        newUser.setEmail("invalidemail");

        when(userRepository.existsByEmailIgnoreCase("invalidemail")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
            userService.createUser(newUser));
    }
}
