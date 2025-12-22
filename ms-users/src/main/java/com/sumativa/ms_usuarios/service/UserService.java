package com.sumativa.ms_usuarios.service;

import com.sumativa.ms_usuarios.dto.LoginResponse;
import com.sumativa.ms_usuarios.dto.RoleResponseDto;
import com.sumativa.ms_usuarios.entity.Role;
import com.sumativa.ms_usuarios.entity.User;
import com.sumativa.ms_usuarios.repository.RoleRepository;
import com.sumativa.ms_usuarios.repository.UserRepository;
import com.sumativa.ms_usuarios.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de usuarios
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Obtiene todos los usuarios
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Obtiene un usuario por ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Obtiene un usuario por email (ignorando mayúsculas/minúsculas)
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    /**
     * Verifica si existe un usuario con el email dado
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    /**
     * Crea un nuevo usuario
     * NOTA: El password se almacena en texto plano según requerimientos de la pauta
     */
    @Transactional
    public User createUser(User user) {
        log.info("Creating user with email: {}", user.getEmail());

        // Verificar que el email no esté en uso
        if (existsByEmail(user.getEmail())) {
            log.warn("Attempt to create user with existing email: {}", user.getEmail());
            throw new IllegalArgumentException("El email ya está registrado: " + user.getEmail());
        }

        // Validación de negocio: Validar dominio de email autorizado
        validateEmailDomain(user.getEmail());

        // Asegurar valores por defecto
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }

        // Procesar roles si existen
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> roles = user.getRoles();
            user.setRoles(Set.of()); // Limpiar para evitar problemas de persistencia

            for (Role role : roles) {
                Role existingRole = roleRepository.findByName(role.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + role.getName()));
                user.getRoles().add(existingRole);
            }
        }

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {} and email: {}", savedUser.getId(), savedUser.getEmail());
        return savedUser;
    }

    /**
     * Actualiza un usuario existente
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("User not found with id: {}", id);
                return new IllegalArgumentException("Usuario no encontrado con id: " + id);
            });

        // Actualizar campos
        if (userDetails.getFullName() != null) {
            user.setFullName(userDetails.getFullName());
        }

        if (userDetails.getEmail() != null && !userDetails.getEmail().equalsIgnoreCase(user.getEmail())) {
            // Verificar que el nuevo email no esté en uso
            if (existsByEmail(userDetails.getEmail())) {
                log.warn("Attempt to update to existing email: {}", userDetails.getEmail());
                throw new IllegalArgumentException("El email ya está registrado: " + userDetails.getEmail());
            }
            user.setEmail(userDetails.getEmail());
        }

        if (userDetails.getPasswordHash() != null) {
            user.setPasswordHash(userDetails.getPasswordHash());
        }

        if (userDetails.getEnabled() != null) {
            user.setEnabled(userDetails.getEnabled());
        }

        // Actualizar roles si se proporcionaron
        if (userDetails.getRoles() != null) {
            user.getRoles().clear();
            for (Role role : userDetails.getRoles()) {
                Role existingRole = roleRepository.findByName(role.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + role.getName()));
                user.getRoles().add(existingRole);
            }
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", updatedUser.getId());
        return updatedUser;
    }

    /**
     * Elimina un usuario por ID
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("User not found with id: {}", id);
                return new IllegalArgumentException("Usuario no encontrado con id: " + id);
            });

        // Validación de negocio: No permitir eliminar usuarios con rol ADMIN
        boolean hasAdminRole = user.getRoles().stream()
            .anyMatch(role -> "ADMIN".equalsIgnoreCase(role.getName()));

        if (hasAdminRole) {
            log.warn("Attempt to delete user with ADMIN role, id: {}", id);
            throw new IllegalArgumentException("No se puede eliminar un usuario con rol ADMIN");
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    /**
     * Habilita o deshabilita un usuario
     */
    @Transactional
    public User toggleUserEnabled(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));

        // Validación de negocio: No permitir deshabilitar el usuario ADMIN principal
        if ("admin@example.com".equalsIgnoreCase(user.getEmail()) && user.getEnabled()) {
            log.warn("Attempt to disable primary ADMIN user: {}", user.getEmail());
            throw new IllegalArgumentException("No se puede deshabilitar el usuario ADMIN principal");
        }

        user.setEnabled(!user.getEnabled());
        return userRepository.save(user);
    }

    /**
     * Asigna un rol a un usuario
     */
    @Transactional
    public User assignRole(Long userId, String roleName) {
        log.info("Assigning role {} to user with id: {}", roleName, userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + userId));

        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName));

        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);
        log.info("Role {} assigned successfully to user with id: {}", roleName, userId);
        return updatedUser;
    }

    /**
     * Remueve un rol de un usuario
     */
    @Transactional
    public User removeRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + userId));

        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName));

        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    /**
     * Inicio de sesión con generación de JWT
     * Valida email y password en texto plano (según pauta) y genera token JWT
     */
    public LoginResponse login(String email, String password) {
        log.info("Login attempt for email: {}", email);

        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> {
                log.warn("Login failed for email: {} (user not found)", email);
                return new IllegalArgumentException("Credenciales inválidas o usuario deshabilitado");
            });

        // Validar password en texto plano (NO loggear contraseña)
        if (!user.getPasswordHash().equals(password)) {
            log.warn("Login failed for email: {} (invalid password)", email);
            throw new IllegalArgumentException("Credenciales inválidas o usuario deshabilitado");
        }

        if (!user.getEnabled()) {
            log.warn("Login failed for email: {} (user disabled)", email);
            throw new IllegalArgumentException("Credenciales inválidas o usuario deshabilitado");
        }

        log.info("Login successful for user: {}", email);

        // Generar token JWT
        List<String> roleNames = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList());
        String token = jwtTokenProvider.generateToken(email, roleNames);

        // Convertir roles a DTOs
        Set<RoleResponseDto> rolesDto = user.getRoles().stream()
            .map(role -> new RoleResponseDto(role.getName()))
            .collect(Collectors.toSet());

        // Crear y retornar LoginResponse con token
        return new LoginResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getEnabled(),
            user.getCreatedAt(),
            rolesDto,
            token
        );
    }

    /**
     * Registro público de usuarios
     * Crea un nuevo usuario con rol USER por defecto
     */
    @Transactional
    public User registerUser(User user) {
        log.info("Registering new user with email: {}", user.getEmail());

        // Crear el usuario usando el método existente
        User createdUser = createUser(user);

        // Asignar rol USER por defecto
        Role userRole = roleRepository.findByName("USER")
            .orElseGet(() -> {
                log.warn("Role USER not found, trying LAB_TECH");
                return roleRepository.findByName("LAB_TECH")
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró un rol por defecto"));
            });

        createdUser.getRoles().add(userRole);
        User savedUser = userRepository.save(createdUser);

        log.info("User registered successfully with id: {} and default role", savedUser.getId());
        return savedUser;
    }

    /**
     * Valida que el email pertenezca a un dominio autorizado
     * Dominios permitidos: duocuc.cl, example.com
     */
    private void validateEmailDomain(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }

        String domain = email.substring(email.lastIndexOf("@") + 1).toLowerCase();
        List<String> allowedDomains = List.of("duocuc.cl", "example.com");

        if (!allowedDomains.contains(domain)) {
            log.warn("Attempt to create user with unauthorized domain: {}", domain);
            throw new IllegalArgumentException("Dominio de email no autorizado. Dominios permitidos: duocuc.cl, example.com");
        }
    }
}
