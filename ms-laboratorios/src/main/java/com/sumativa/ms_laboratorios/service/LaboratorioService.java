package com.sumativa.ms_laboratorios.service;

import com.sumativa.ms_laboratorios.entity.Laboratorio;
import com.sumativa.ms_laboratorios.repository.LaboratorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de laboratorios
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;

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
        if (laboratorio.getId() != null) {
            throw new IllegalArgumentException("El ID debe ser nulo al crear un nuevo laboratorio");
        }
        return laboratorioRepository.save(laboratorio);
    }

    /**
     * Actualizar laboratorio existente
     */
    @Transactional
    public Laboratorio update(Long id, Laboratorio laboratorio) {
        Laboratorio existing = laboratorioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Laboratorio no encontrado con id: " + id));

        existing.setNombre(laboratorio.getNombre());
        existing.setDireccion(laboratorio.getDireccion());
        existing.setTelefono(laboratorio.getTelefono());

        return laboratorioRepository.save(existing);
    }

    /**
     * Eliminar laboratorio
     */
    @Transactional
    public void delete(Long id) {
        if (!laboratorioRepository.existsById(id)) {
            throw new IllegalArgumentException("Laboratorio no encontrado con id: " + id);
        }
        laboratorioRepository.deleteById(id);
    }
}
