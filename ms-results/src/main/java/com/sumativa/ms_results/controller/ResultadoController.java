package com.sumativa.ms_results.controller;

import com.sumativa.ms_results.dto.ResultadoCreateDto;
import com.sumativa.ms_results.dto.ResultadoResponseDto;
import com.sumativa.ms_results.dto.ResultadoUpdateDto;
import com.sumativa.ms_results.entity.Resultado;
import com.sumativa.ms_results.entity.TipoAnalisis;
import com.sumativa.ms_results.mapper.ResultadoMapper;
import com.sumativa.ms_results.service.ResultadoService;
import com.sumativa.ms_results.service.TipoAnalisisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de Resultados de análisis.
 * Utiliza DTOs para entrada/salida (no expone entidades directamente).
 */
@Slf4j
@RestController
@RequestMapping("/api/resultados")
@RequiredArgsConstructor
public class ResultadoController {

    private final ResultadoService resultadoService;
    private final TipoAnalisisService tipoAnalisisService;
    private final ResultadoMapper resultadoMapper;

    /**
     * GET /api/resultados - Lista todos los resultados
     */
    @GetMapping
    public ResponseEntity<List<ResultadoResponseDto>> getAll() {
        log.info("GET /api/resultados - Obteniendo todos los resultados");
        List<Resultado> resultados = resultadoService.findAll();
        List<ResultadoResponseDto> dtos = resultados.stream()
                .map(resultadoMapper::toDto)
                .collect(Collectors.toList());
        log.info("Retornando {} resultados", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/resultados/{id} - Obtiene un resultado por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResultadoResponseDto> getById(@PathVariable Long id) {
        log.info("GET /api/resultados/{} - Obteniendo resultado", id);
        Resultado resultado = resultadoService.findById(id);
        ResultadoResponseDto dto = resultadoMapper.toDto(resultado);
        return ResponseEntity.ok(dto);
    }

    /**
     * POST /api/resultados - Crea un nuevo resultado
     */
    @PostMapping
    public ResponseEntity<ResultadoResponseDto> create(@Valid @RequestBody ResultadoCreateDto createDto) {
        log.info("POST /api/resultados - Creando nuevo resultado para paciente: {}", createDto.getPaciente());

        // Buscar el TipoAnalisis
        TipoAnalisis tipoAnalisis = tipoAnalisisService.findById(createDto.getTipoAnalisisId());

        // Convertir DTO a Entity
        Resultado resultado = resultadoMapper.toEntity(createDto, tipoAnalisis);

        // Guardar
        Resultado created = resultadoService.create(resultado);

        // Convertir Entity a DTO de respuesta
        ResultadoResponseDto responseDto = resultadoMapper.toDto(created);

        log.info("Resultado creado exitosamente con ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * PUT /api/resultados/{id} - Actualiza un resultado existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResultadoResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ResultadoUpdateDto updateDto) {

        log.info("PUT /api/resultados/{} - Actualizando resultado", id);

        // Buscar el resultado existente
        Resultado resultado = resultadoService.findById(id);

        // Buscar el TipoAnalisis si se cambió
        TipoAnalisis tipoAnalisis = null;
        if (updateDto.getTipoAnalisisId() != null) {
            tipoAnalisis = tipoAnalisisService.findById(updateDto.getTipoAnalisisId());
        }

        // Actualizar campos
        resultadoMapper.updateEntityFromDto(updateDto, resultado, tipoAnalisis);

        // Guardar
        Resultado updated = resultadoService.update(id, resultado);

        // Convertir a DTO de respuesta
        ResultadoResponseDto responseDto = resultadoMapper.toDto(updated);

        log.info("Resultado {} actualizado exitosamente", id);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * DELETE /api/resultados/{id} - Elimina un resultado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/resultados/{} - Eliminando resultado", id);
        resultadoService.delete(id);
        log.info("Resultado {} eliminado exitosamente", id);
        return ResponseEntity.noContent().build();
    }
}
