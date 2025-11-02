package com.sumativa.ms_usuarios.controller;

import com.sumativa.ms_usuarios.dto.LoginRequest;
import com.sumativa.ms_usuarios.dto.LoginResponse;
import com.sumativa.ms_usuarios.entity.User;
import com.sumativa.ms_usuarios.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para gestión de usuarios
 * Base URL: /api/users
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users
     * Obtiene todos los usuarios
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/{id}
     * Obtiene un usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/users/email/{email}
     * Obtiene un usuario por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
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
     *   "passwordHash": "password123",
     *   "enabled": true
     * }
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/users/{id}
     * Actualiza un usuario existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/users/{id}
     * Elimina un usuario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/users/{id}/toggle-enabled
     * Habilita/deshabilita un usuario
     */
    @PatchMapping("/{id}/toggle-enabled")
    public ResponseEntity<?> toggleUserEnabled(@PathVariable Long id) {
        try {
            User user = userService.toggleUserEnabled(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/users/{id}/roles/{roleName}
     * Asigna un rol a un usuario
     *
     * Ejemplo: POST /api/users/1/roles/ADMIN
     */
    @PostMapping("/{id}/roles/{roleName}")
    public ResponseEntity<?> assignRole(@PathVariable Long id, @PathVariable String roleName) {
        try {
            User user = userService.assignRole(id, roleName);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/users/{id}/roles/{roleName}
     * Remueve un rol de un usuario
     *
     * Ejemplo: DELETE /api/users/1/roles/ADMIN
     */
    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<?> removeRole(@PathVariable Long id, @PathVariable String roleName) {
        try {
            User user = userService.removeRole(id, roleName);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userService.login(loginRequest.getEmail(), loginRequest.getPassword());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            LoginResponse response = new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getEnabled(),
                user.getCreatedAt(),
                user.getRoles()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales inválidas o usuario deshabilitado"));
        }
    }
}
