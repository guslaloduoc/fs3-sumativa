package com.sumativa.ms_usuarios.service;

import com.sumativa.ms_usuarios.entity.Role;
import com.sumativa.ms_usuarios.entity.User;
import com.sumativa.ms_usuarios.repository.RoleRepository;
import com.sumativa.ms_usuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
     * Inicio de sesión básico (sin cifrado, según pauta)
     * Valida email y password en texto plano
     */
    public Optional<User> login(String email, String password) {
        log.info("Login attempt for email: {}", email);

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Validar password en texto plano (NO loggear contraseña)
            if (user.getPasswordHash().equals(password) && user.getEnabled()) {
                log.info("Login successful for user: {}", email);
                return Optional.of(user);
            }
        }

        log.warn("Login failed for email: {} (invalid credentials or disabled user)", email);
        return Optional.empty();
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
