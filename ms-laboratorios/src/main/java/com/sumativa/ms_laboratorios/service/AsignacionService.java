package com.sumativa.ms_laboratorios.service;

import com.sumativa.ms_laboratorios.entity.Asignacion;
import com.sumativa.ms_laboratorios.entity.Laboratorio;
import com.sumativa.ms_laboratorios.repository.AsignacionRepository;
import com.sumativa.ms_laboratorios.repository.LaboratorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de asignaciones
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final LaboratorioRepository laboratorioRepository;

    /**
     * Obtener todas las asignaciones
     */
    public List<Asignacion> findAll() {
        return asignacionRepository.findAll();
    }

    /**
     * Obtener asignación por ID
     */
    public Optional<Asignacion> findById(Long id) {
        return asignacionRepository.findById(id);
    }

    /**
     * Crear nueva asignación
     */
    @Transactional
    public Asignacion create(Asignacion asignacion) {
        if (asignacion.getId() != null) {
            throw new IllegalArgumentException("El ID debe ser nulo al crear una nueva asignación");
        }

        // Validar que el laboratorio existe
        if (asignacion.getLaboratorio() != null && asignacion.getLaboratorio().getId() != null) {
            Laboratorio laboratorio = laboratorioRepository.findById(asignacion.getLaboratorio().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Laboratorio no encontrado con id: " + asignacion.getLaboratorio().getId()));
            asignacion.setLaboratorio(laboratorio);
        }

        return asignacionRepository.save(asignacion);
    }

    /**
     * Actualizar asignación existente
     */
    @Transactional
    public Asignacion update(Long id, Asignacion asignacion) {
        Asignacion existing = asignacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada con id: " + id));

        existing.setPaciente(asignacion.getPaciente());
        existing.setFecha(asignacion.getFecha());

        // Actualizar laboratorio si se proporciona
        if (asignacion.getLaboratorio() != null && asignacion.getLaboratorio().getId() != null) {
            Laboratorio laboratorio = laboratorioRepository.findById(asignacion.getLaboratorio().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Laboratorio no encontrado con id: " + asignacion.getLaboratorio().getId()));
            existing.setLaboratorio(laboratorio);
        }

        return asignacionRepository.save(existing);
    }

    /**
     * Eliminar asignación
     */
    @Transactional
    public void delete(Long id) {
        if (!asignacionRepository.existsById(id)) {
            throw new IllegalArgumentException("Asignación no encontrada con id: " + id);
        }
        asignacionRepository.deleteById(id);
    }
}
