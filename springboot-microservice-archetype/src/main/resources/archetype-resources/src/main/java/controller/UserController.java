#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.controller;

import ${package}.dto.*;
import ${package}.entity.User;
import ${package}.mapper.UserMapper;
import ${package}.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de usuarios
 * Base URL: /api/users
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users
     * Obtiene todos los usuarios
     * @return Lista de UserResponseDto (sin passwordHash)
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        log.debug("GET /api/users - Fetching all users");
        List<User> users = userService.findAll();
        List<UserResponseDto> responseDtos = users.stream()
            .map(UserMapper::toResponseDto)
            .collect(Collectors.toList());
        log.debug("Returning {} users", responseDtos.size());
        return ResponseEntity.ok(responseDtos);
    }

    /**
     * GET /api/users/{id}
     * Obtiene un usuario por ID
     * @return UserResponseDto (sin passwordHash)
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return userService.findById(id)
            .map(UserMapper::toResponseDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/users/email/{email}
     * Obtiene un usuario por email
     * @return UserResponseDto (sin passwordHash)
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
            .map(UserMapper::toResponseDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/users
     * Crea un nuevo usuario
     *
     * Body ejemplo:
     * {
     *   "fullName": "Juan Pérez",
     *   "email": "juan@example.com",
     *   "password": "password123",
     *   "enabled": true
     * }
     * @return UserResponseDto del usuario creado (sin passwordHash)
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto createDto) {
        User user = UserMapper.toEntity(createDto);
        User createdUser = userService.createUser(user);
        UserResponseDto responseDto = UserMapper.toResponseDto(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * PUT /api/users/{id}
     * Actualiza un usuario existente
     *
     * Body ejemplo (todos los campos opcionales):
     * {
     *   "fullName": "Juan Pérez Updated",
     *   "email": "newemail@example.com",
     *   "password": "newpassword123",
     *   "enabled": false
     * }
     * @return UserResponseDto del usuario actualizado (sin passwordHash)
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto updateDto) {
        User user = userService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));

        UserMapper.updateEntityFromDto(user, updateDto);
        User updatedUser = userService.updateUser(id, user);
        UserResponseDto responseDto = UserMapper.toResponseDto(updatedUser);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * DELETE /api/users/{id}
     * Elimina un usuario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado exitosamente"));
    }

    /**
     * PATCH /api/users/{id}/toggle-enabled
     * Habilita/deshabilita un usuario
     * @return UserResponseDto del usuario actualizado (sin passwordHash)
     */
    @PatchMapping("/{id}/toggle-enabled")
    public ResponseEntity<UserResponseDto> toggleUserEnabled(@PathVariable Long id) {
        User user = userService.toggleUserEnabled(id);
        UserResponseDto responseDto = UserMapper.toResponseDto(user);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * POST /api/users/{id}/roles/{roleName}
     * Asigna un rol a un usuario
     *
     * Ejemplo: POST /api/users/1/roles/ADMIN
     * @return UserResponseDto con roles actualizados (sin passwordHash)
     */
    @PostMapping("/{id}/roles/{roleName}")
    public ResponseEntity<UserResponseDto> assignRole(@PathVariable Long id, @PathVariable String roleName) {
        User user = userService.assignRole(id, roleName);
        UserResponseDto responseDto = UserMapper.toResponseDto(user);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * DELETE /api/users/{id}/roles/{roleName}
     * Remueve un rol de un usuario
     *
     * Ejemplo: DELETE /api/users/1/roles/ADMIN
     * @return UserResponseDto con roles actualizados (sin passwordHash)
     */
    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<UserResponseDto> removeRole(@PathVariable Long id, @PathVariable String roleName) {
        User user = userService.removeRole(id, roleName);
        UserResponseDto responseDto = UserMapper.toResponseDto(user);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * POST /api/users/login
     * Inicio de sesión - Valida credenciales y retorna información del usuario
     *
     * Body ejemplo:
     * {
     *   "email": "admin@example.com",
     *   "password": "admin123"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword())
            .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas o usuario deshabilitado"));

        LoginResponse response = UserMapper.toLoginResponse(user);
        return ResponseEntity.ok(response);
    }
}
