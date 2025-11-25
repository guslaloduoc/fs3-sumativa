package com.sumativa.ms_results.controller;

import com.sumativa.ms_results.dto.TipoAnalisisCreateDto;
import com.sumativa.ms_results.dto.TipoAnalisisResponseDto;
import com.sumativa.ms_results.dto.TipoAnalisisUpdateDto;
import com.sumativa.ms_results.entity.TipoAnalisis;
import com.sumativa.ms_results.mapper.TipoAnalisisMapper;
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
 * Controlador REST para gestión de Tipos de Análisis.
 * Utiliza DTOs para entrada/salida (no expone entidades directamente).
 */
@Slf4j
@RestController
@RequestMapping("/api/tipos-analisis")
@RequiredArgsConstructor
public class TipoAnalisisController {

    private final TipoAnalisisService tipoAnalisisService;
    private final TipoAnalisisMapper tipoAnalisisMapper;

    /**
     * GET /api/tipos-analisis - Lista todos los tipos de análisis
     */
    @GetMapping
    public ResponseEntity<List<TipoAnalisisResponseDto>> getAll() {
        log.info("GET /api/tipos-analisis - Obteniendo todos los tipos de análisis");
        List<TipoAnalisis> tipos = tipoAnalisisService.findAll();
        List<TipoAnalisisResponseDto> dtos = tipos.stream()
                .map(tipoAnalisisMapper::toDto)
                .collect(Collectors.toList());
        log.info("Retornando {} tipos de análisis", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/tipos-analisis/{id} - Obtiene un tipo de análisis por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipoAnalisisResponseDto> getById(@PathVariable Long id) {
        log.info("GET /api/tipos-analisis/{} - Obteniendo tipo de análisis", id);
        TipoAnalisis tipoAnalisis = tipoAnalisisService.findById(id);
        TipoAnalisisResponseDto dto = tipoAnalisisMapper.toDto(tipoAnalisis);
        return ResponseEntity.ok(dto);
    }

    /**
     * POST /api/tipos-analisis - Crea un nuevo tipo de análisis
     */
    @PostMapping
    public ResponseEntity<TipoAnalisisResponseDto> create(@Valid @RequestBody TipoAnalisisCreateDto createDto) {
        log.info("POST /api/tipos-analisis - Creando nuevo tipo de análisis: {}", createDto.getNombre());

        // Convertir DTO a Entity
        TipoAnalisis tipoAnalisis = tipoAnalisisMapper.toEntity(createDto);

        // Guardar
        TipoAnalisis created = tipoAnalisisService.create(tipoAnalisis);

        // Convertir Entity a DTO de respuesta
        TipoAnalisisResponseDto responseDto = tipoAnalisisMapper.toDto(created);

        log.info("Tipo de análisis creado exitosamente con ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * PUT /api/tipos-analisis/{id} - Actualiza un tipo de análisis existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<TipoAnalisisResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TipoAnalisisUpdateDto updateDto) {

        log.info("PUT /api/tipos-analisis/{} - Actualizando tipo de análisis", id);

        // Buscar el tipo existente
        TipoAnalisis tipoAnalisis = tipoAnalisisService.findById(id);

        // Actualizar campos
        tipoAnalisisMapper.updateEntityFromDto(updateDto, tipoAnalisis);

        // Guardar
        TipoAnalisis updated = tipoAnalisisService.update(id, tipoAnalisis);

        // Convertir a DTO de respuesta
        TipoAnalisisResponseDto responseDto = tipoAnalisisMapper.toDto(updated);

        log.info("Tipo de análisis {} actualizado exitosamente", id);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * DELETE /api/tipos-analisis/{id} - Elimina un tipo de análisis
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/tipos-analisis/{} - Eliminando tipo de análisis", id);
        tipoAnalisisService.delete(id);
        log.info("Tipo de análisis {} eliminado exitosamente", id);
        return ResponseEntity.noContent().build();
    }
}
