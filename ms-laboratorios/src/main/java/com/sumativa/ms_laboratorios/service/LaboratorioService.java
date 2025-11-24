package com.sumativa.ms_laboratorios.service;

import com.sumativa.ms_laboratorios.entity.Laboratorio;
import com.sumativa.ms_laboratorios.repository.AsignacionRepository;
import com.sumativa.ms_laboratorios.repository.LaboratorioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de laboratorios
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;
    private final AsignacionRepository asignacionRepository;

    /**
     * Obtener todos los laboratorios
     */
    public List<Laboratorio> findAll() {
        return laboratorioRepository.findAll();
    }

    /**
     * Obtener laboratorio por ID
     */
    public Optional<Laboratorio> findById(Long id) {
        return laboratorioRepository.findById(id);
    }

    /**
     * Crear nuevo laboratorio
     */
    @Transactional
    public Laboratorio create(Laboratorio laboratorio) {
        log.info("Creating laboratorio with nombre: {}", laboratorio.getNombre());

        if (laboratorio.getId() != null) {
            log.warn("Attempt to create laboratorio with non-null ID");
            throw new IllegalArgumentException("El ID debe ser nulo al crear un nuevo laboratorio");
        }

        // Validación de negocio: Nombre único
        validateUniqueNombre(laboratorio.getNombre(), null);

        // Validación de negocio: Formato de teléfono
        if (laboratorio.getTelefono() != null && !laboratorio.getTelefono().isEmpty()) {
            validatePhoneFormat(laboratorio.getTelefono());
        }

        Laboratorio saved = laboratorioRepository.save(laboratorio);
        log.info("Laboratorio created successfully with id: {}", saved.getId());
        return saved;
    }

    /**
     * Actualizar laboratorio existente
     */
    @Transactional
    public Laboratorio update(Long id, Laboratorio laboratorio) {
        log.info("Updating laboratorio with id: {}", id);

        Laboratorio existing = laboratorioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Laboratorio not found with id: {}", id);
                    return new IllegalArgumentException("Laboratorio no encontrado con id: " + id);
                });

        // Validación de negocio: Nombre único (excluir el actual)
        if (!existing.getNombre().equals(laboratorio.getNombre())) {
            validateUniqueNombre(laboratorio.getNombre(), id);
        }

        // Validación de negocio: Formato de teléfono
        if (laboratorio.getTelefono() != null && !laboratorio.getTelefono().isEmpty()) {
            validatePhoneFormat(laboratorio.getTelefono());
        }

        existing.setNombre(laboratorio.getNombre());
        existing.setDireccion(laboratorio.getDireccion());
        existing.setTelefono(laboratorio.getTelefono());

        Laboratorio updated = laboratorioRepository.save(existing);
        log.info("Laboratorio updated successfully with id: {}", updated.getId());
        return updated;
    }

    /**
     * Eliminar laboratorio
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting laboratorio with id: {}", id);

        Laboratorio laboratorio = laboratorioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Laboratorio not found with id: {}", id);
                    return new IllegalArgumentException("Laboratorio no encontrado con id: " + id);
                });

        // Validación de negocio: No eliminar laboratorios con asignaciones activas
        long asignacionesCount = asignacionRepository.countByLaboratorioId(id);
        if (asignacionesCount > 0) {
            log.warn("Attempt to delete laboratorio with {} active asignaciones, id: {}", asignacionesCount, id);
            throw new IllegalArgumentException(
                    "No se puede eliminar el laboratorio porque tiene " + asignacionesCount + " asignaciones activas");
        }

        laboratorioRepository.deleteById(id);
        log.info("Laboratorio deleted successfully with id: {}", id);
    }

    /**
     * Valida que el nombre del laboratorio sea único
     */
    private void validateUniqueNombre(String nombre, Long excludeId) {
        Optional<Laboratorio> existing = laboratorioRepository.findByNombreIgnoreCase(nombre);
        if (existing.isPresent() && !existing.get().getId().equals(excludeId)) {
            log.warn("Attempt to create/update laboratorio with duplicate nombre: {}", nombre);
            throw new IllegalArgumentException("Ya existe un laboratorio con el nombre: " + nombre);
        }
    }

    /**
     * Valida el formato del teléfono (7-15 dígitos numéricos)
     */
    private void validatePhoneFormat(String telefono) {
        // Eliminar espacios, guiones y paréntesis para validar
        String cleanPhone = telefono.replaceAll("[\\s\\-()]", "");

        if (!cleanPhone.matches("\\d{7,15}")) {
            log.warn("Invalid phone format: {}", telefono);
            throw new IllegalArgumentException("Formato de teléfono inválido. Debe contener entre 7 y 15 dígitos");
        }
    }
}
