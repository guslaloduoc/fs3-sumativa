package com.sumativa.ms_results.service;

import com.sumativa.ms_results.entity.Resultado;
import com.sumativa.ms_results.entity.TipoAnalisis;
import com.sumativa.ms_results.repository.ResultadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ResultadoService {

    private final ResultadoRepository resultadoRepository;
    private final TipoAnalisisService tipoAnalisisService;

    public List<Resultado> findAll() {
        log.info("Buscando todos los resultados");
        return resultadoRepository.findAll();
    }

    public Resultado findById(Long id) {
        log.info("Buscando resultado con ID: {}", id);
        return resultadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Resultado con ID {} no encontrado", id);
                    return new IllegalArgumentException("Resultado no encontrado con ID: " + id);
                });
    }

    @Transactional
    public Resultado create(Resultado resultado) {
        log.info("Creando nuevo resultado para paciente: {}", resultado.getPaciente());

        // Validar que el tipo de análisis existe
        if (resultado.getTipoAnalisis() != null && resultado.getTipoAnalisis().getId() != null) {
            TipoAnalisis tipoAnalisis = tipoAnalisisService.findById(resultado.getTipoAnalisis().getId());
            resultado.setTipoAnalisis(tipoAnalisis);
        } else {
            log.warn("El tipo de análisis es requerido");
            throw new IllegalArgumentException("El tipo de análisis es requerido");
        }

        Resultado saved = resultadoRepository.save(resultado);
        log.info("Resultado creado exitosamente con ID: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Resultado update(Long id, Resultado resultado) {
        log.info("Actualizando resultado con ID: {}", id);

        Resultado existing = findById(id);

        // Validar que el tipo de análisis existe si se está actualizando
        if (resultado.getTipoAnalisis() != null && resultado.getTipoAnalisis().getId() != null) {
            TipoAnalisis tipoAnalisis = tipoAnalisisService.findById(resultado.getTipoAnalisis().getId());
            existing.setTipoAnalisis(tipoAnalisis);
        }

        existing.setPaciente(resultado.getPaciente());
        existing.setFechaRealizacion(resultado.getFechaRealizacion());
        existing.setLaboratorioId(resultado.getLaboratorioId());
        existing.setValorNumerico(resultado.getValorNumerico());
        existing.setValorTexto(resultado.getValorTexto());
        existing.setEstado(resultado.getEstado());
        existing.setObservaciones(resultado.getObservaciones());

        Resultado updated = resultadoRepository.save(existing);
        log.info("Resultado actualizado exitosamente con ID: {}", updated.getId());
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Eliminando resultado con ID: {}", id);

        Resultado resultado = findById(id);
        resultadoRepository.delete(resultado);

        log.info("Resultado eliminado exitosamente con ID: {}", id);
    }
}
