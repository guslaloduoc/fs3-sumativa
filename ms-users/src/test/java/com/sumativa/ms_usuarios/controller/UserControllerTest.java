package com.sumativa.ms_usuarios.controller;

import com.sumativa.ms_usuarios.dto.*;
import com.sumativa.ms_usuarios.entity.Role;
import com.sumativa.ms_usuarios.entity.User;
import com.sumativa.ms_usuarios.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ADMIN");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("password123");
        testUser.setEnabled(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setRoles(new HashSet<>(Set.of(adminRole)));
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        when(userService.findAll()).thenReturn(List.of(testUser));

        ResponseEntity<List<UserResponseDto>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("test@example.com", response.getBody().get(0).getEmail());
    }

    @Test
    void getAllUsers_shouldReturnEmptyList() {
        when(userService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserResponseDto>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<UserResponseDto> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    void getUserById_shouldReturn404_whenNotExists() {
        when(userService.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<UserResponseDto> response = userController.getUserById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUserByEmail_shouldReturnUser_whenExists() {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        ResponseEntity<UserResponseDto> response = userController.getUserByEmail("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test User", response.getBody().getFullName());
    }

    @Test
    void getUserByEmail_shouldReturn404_whenNotExists() {
        when(userService.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        ResponseEntity<UserResponseDto> response = userController.getUserByEmail("notfound@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createUser_shouldReturnCreatedUser() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setFullName("New User");
        createDto.setEmail("new@example.com");
        createDto.setPassword("password123");
        createDto.setEnabled(true);

        when(userService.createUser(any(User.class))).thenReturn(testUser);

        ResponseEntity<UserResponseDto> response = userController.createUser(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userService).createUser(any(User.class));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setFullName("Updated Name");

        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(testUser);

        ResponseEntity<UserResponseDto> response = userController.updateUser(1L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).updateUser(eq(1L), any(User.class));
    }

    @Test
    void updateUser_shouldThrowException_whenNotFound() {
        UserUpdateDto updateDto = new UserUpdateDto();
        when(userService.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            userController.updateUser(999L, updateDto));
    }

    @Test
    void deleteUser_shouldReturnSuccessMessage() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Map<String, String>> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuario eliminado exitosamente", response.getBody().get("message"));
    }

    @Test
    void toggleUserEnabled_shouldReturnUpdatedUser() {
        testUser.setEnabled(false);
        when(userService.toggleUserEnabled(1L)).thenReturn(testUser);

        ResponseEntity<UserResponseDto> response = userController.toggleUserEnabled(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getEnabled());
    }

    @Test
    void assignRole_shouldReturnUserWithNewRole() {
        when(userService.assignRole(1L, "DOCTOR")).thenReturn(testUser);

        ResponseEntity<UserResponseDto> response = userController.assignRole(1L, "DOCTOR");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).assignRole(1L, "DOCTOR");
    }

    @Test
    void removeRole_shouldReturnUserWithoutRole() {
        when(userService.removeRole(1L, "ADMIN")).thenReturn(testUser);

        ResponseEntity<UserResponseDto> response = userController.removeRole(1L, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).removeRole(1L, "ADMIN");
    }

    @Test
    void login_shouldReturnLoginResponse() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        LoginResponse loginResponse = new LoginResponse(
            1L, "Test User", "test@example.com", true,
            LocalDateTime.now(), Set.of(new RoleResponseDto("ADMIN")), "jwt.token.here"
        );

        when(userService.login("test@example.com", "password123")).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = userController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt.token.here", response.getBody().getToken());
    }
}
