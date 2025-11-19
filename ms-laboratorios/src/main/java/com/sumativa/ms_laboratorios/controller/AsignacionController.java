package com.sumativa.ms_laboratorios.controller;

import com.sumativa.ms_laboratorios.dto.AsignacionCreateDto;
import com.sumativa.ms_laboratorios.dto.AsignacionResponseDto;
import com.sumativa.ms_laboratorios.dto.AsignacionUpdateDto;
import com.sumativa.ms_laboratorios.entity.Asignacion;
import com.sumativa.ms_laboratorios.entity.Laboratorio;
import com.sumativa.ms_laboratorios.mapper.AsignacionMapper;
import com.sumativa.ms_laboratorios.service.AsignacionService;
import com.sumativa.ms_laboratorios.service.LaboratorioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de asignaciones
 */
@RestController
@RequestMapping("/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionService asignacionService;
    private final LaboratorioService laboratorioService;

    /**
     * GET /asignaciones - Listar todas las asignaciones
     * @return Lista de AsignacionResponseDto
     */
    @GetMapping
    public ResponseEntity<List<AsignacionResponseDto>> findAll() {
        List<Asignacion> asignaciones = asignacionService.findAll();
        List<AsignacionResponseDto> responseDtos = asignaciones.stream()
            .map(AsignacionMapper::toResponseDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    /**
     * GET /asignaciones/{id} - Obtener asignación por ID
     * @return AsignacionResponseDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<AsignacionResponseDto> findById(@PathVariable Long id) {
        Asignacion asignacion = asignacionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada con id: " + id));
        return ResponseEntity.ok(AsignacionMapper.toResponseDto(asignacion));
    }

    /**
     * POST /asignaciones - Crear nueva asignación
     * Body ejemplo: { "paciente": "Juan Pérez", "fecha": "2025-01-15", "laboratorioId": 1 }
     * @return AsignacionResponseDto de la asignación creada
     */
    @PostMapping
    public ResponseEntity<AsignacionResponseDto> create(@Valid @RequestBody AsignacionCreateDto createDto) {
        // Recuperar el laboratorio por ID
        Laboratorio laboratorio = laboratorioService.findById(createDto.getLaboratorioId())
                .orElseThrow(() -> new IllegalArgumentException("Laboratorio no encontrado con id: " + createDto.getLaboratorioId()));

        Asignacion asignacion = AsignacionMapper.toEntity(createDto, laboratorio);
        Asignacion created = asignacionService.create(asignacion);
        AsignacionResponseDto responseDto = AsignacionMapper.toResponseDto(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * PUT /asignaciones/{id} - Actualizar asignación existente
     * @param updateDto Datos de la asignación a actualizar (campos opcionales)
     * @return AsignacionResponseDto de la asignación actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<AsignacionResponseDto> update(@PathVariable Long id, @Valid @RequestBody AsignacionUpdateDto updateDto) {
        Asignacion asignacion = asignacionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada con id: " + id));

        // Si se proporciona un nuevo laboratorioId, recuperarlo
        Laboratorio laboratorio = null;
        if (updateDto.getLaboratorioId() != null) {
            laboratorio = laboratorioService.findById(updateDto.getLaboratorioId())
                    .orElseThrow(() -> new IllegalArgumentException("Laboratorio no encontrado con id: " + updateDto.getLaboratorioId()));
        }

        AsignacionMapper.updateEntityFromDto(asignacion, updateDto, laboratorio);
        Asignacion updated = asignacionService.update(id, asignacion);
        AsignacionResponseDto responseDto = AsignacionMapper.toResponseDto(updated);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * DELETE /asignaciones/{id} - Eliminar asignación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        asignacionService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Asignación eliminada exitosamente"));
    }
}
