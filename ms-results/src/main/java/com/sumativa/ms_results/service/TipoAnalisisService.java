package com.sumativa.ms_results.service;

import com.sumativa.ms_results.entity.TipoAnalisis;
import com.sumativa.ms_results.repository.TipoAnalisisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TipoAnalisisService {

    private final TipoAnalisisRepository tipoAnalisisRepository;

    public List<TipoAnalisis> findAll() {
        log.info("Buscando todos los tipos de análisis");
        return tipoAnalisisRepository.findAll();
    }

    public TipoAnalisis findById(Long id) {
        log.info("Buscando tipo de análisis con ID: {}", id);
        return tipoAnalisisRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tipo de análisis con ID {} no encontrado", id);
                    return new IllegalArgumentException("Tipo de análisis no encontrado con ID: " + id);
                });
    }

    @Transactional
    public TipoAnalisis create(TipoAnalisis tipoAnalisis) {
        log.info("Creando nuevo tipo de análisis: {}", tipoAnalisis.getNombre());

        // Validar que no exista otro con el mismo nombre
        tipoAnalisisRepository.findByNombreIgnoreCase(tipoAnalisis.getNombre())
                .ifPresent(existing -> {
                    log.warn("Ya existe un tipo de análisis con nombre: {}", tipoAnalisis.getNombre());
                    throw new IllegalArgumentException("Ya existe un tipo de análisis con el nombre: " + tipoAnalisis.getNombre());
                });

        TipoAnalisis saved = tipoAnalisisRepository.save(tipoAnalisis);
        log.info("Tipo de análisis creado exitosamente con ID: {}", saved.getId());
        return saved;
    }

    @Transactional
    public TipoAnalisis update(Long id, TipoAnalisis tipoAnalisis) {
        log.info("Actualizando tipo de análisis con ID: {}", id);

        TipoAnalisis existing = findById(id);

        // Validar que no exista otro con el mismo nombre (excepto el actual)
        tipoAnalisisRepository.findByNombreIgnoreCase(tipoAnalisis.getNombre())
                .ifPresent(other -> {
                    if (!other.getId().equals(id)) {
                        log.warn("Ya existe otro tipo de análisis con nombre: {}", tipoAnalisis.getNombre());
                        throw new IllegalArgumentException("Ya existe otro tipo de análisis con el nombre: " + tipoAnalisis.getNombre());
                    }
                });

        existing.setNombre(tipoAnalisis.getNombre());
        existing.setCategoria(tipoAnalisis.getCategoria());
        existing.setUnidadMedida(tipoAnalisis.getUnidadMedida());
        existing.setValorReferenciaMin(tipoAnalisis.getValorReferenciaMin());
        existing.setValorReferenciaMax(tipoAnalisis.getValorReferenciaMax());
        existing.setActivo(tipoAnalisis.getActivo());

        TipoAnalisis updated = tipoAnalisisRepository.save(existing);
        log.info("Tipo de análisis actualizado exitosamente con ID: {}", updated.getId());
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Eliminando tipo de análisis con ID: {}", id);

        TipoAnalisis tipoAnalisis = findById(id);
        tipoAnalisisRepository.delete(tipoAnalisis);

        log.info("Tipo de análisis eliminado exitosamente con ID: {}", id);
    }
}
