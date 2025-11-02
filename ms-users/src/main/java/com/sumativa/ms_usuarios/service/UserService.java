package com.sumativa.ms_usuarios.service;

import com.sumativa.ms_usuarios.entity.Role;
import com.sumativa.ms_usuarios.entity.User;
import com.sumativa.ms_usuarios.repository.RoleRepository;
import com.sumativa.ms_usuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio para gestión de usuarios
 */
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
        // Verificar que el email no esté en uso
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado: " + user.getEmail());
        }

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

        return userRepository.save(user);
    }

    /**
     * Actualiza un usuario existente
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));

        // Actualizar campos
        if (userDetails.getFullName() != null) {
            user.setFullName(userDetails.getFullName());
        }

        if (userDetails.getEmail() != null && !userDetails.getEmail().equalsIgnoreCase(user.getEmail())) {
            // Verificar que el nuevo email no esté en uso
            if (existsByEmail(userDetails.getEmail())) {
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

        return userRepository.save(user);
    }

    /**
     * Elimina un usuario por ID
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Habilita o deshabilita un usuario
     */
    @Transactional
    public User toggleUserEnabled(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));
        user.setEnabled(!user.getEnabled());
        return userRepository.save(user);
    }

    /**
     * Asigna un rol a un usuario
     */
    @Transactional
    public User assignRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + userId));

        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName));

        user.getRoles().add(role);
        return userRepository.save(user);
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
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Validar password en texto plano
            if (user.getPasswordHash().equals(password) && user.getEnabled()) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }
}
