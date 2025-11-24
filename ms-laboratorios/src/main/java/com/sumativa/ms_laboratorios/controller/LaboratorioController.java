package com.sumativa.ms_laboratorios.controller;

import com.sumativa.ms_laboratorios.dto.LaboratorioCreateDto;
import com.sumativa.ms_laboratorios.dto.LaboratorioResponseDto;
import com.sumativa.ms_laboratorios.dto.LaboratorioUpdateDto;
import com.sumativa.ms_laboratorios.entity.Laboratorio;
import com.sumativa.ms_laboratorios.mapper.LaboratorioMapper;
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
 * Controlador REST para gestión de laboratorios
 */
@RestController
@RequestMapping("/laboratorios")
@RequiredArgsConstructor
public class LaboratorioController {

    private final LaboratorioService laboratorioService;

    /**
     * GET /laboratorios - Listar todos los laboratorios
     * @return Lista de LaboratorioResponseDto
     */
    @GetMapping
    public ResponseEntity<List<LaboratorioResponseDto>> findAll() {
        List<Laboratorio> laboratorios = laboratorioService.findAll();
        List<LaboratorioResponseDto> responseDtos = laboratorios.stream()
            .map(LaboratorioMapper::toResponseDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    /**
     * GET /laboratorios/{id} - Obtener laboratorio por ID
     * @return LaboratorioResponseDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<LaboratorioResponseDto> findById(@PathVariable Long id) {
        Laboratorio laboratorio = laboratorioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Laboratorio no encontrado con id: " + id));
        return ResponseEntity.ok(LaboratorioMapper.toResponseDto(laboratorio));
    }

    /**
     * POST /laboratorios - Crear nuevo laboratorio
     * @param createDto Datos del laboratorio a crear
     * @return LaboratorioResponseDto del laboratorio creado
     */
    @PostMapping
    public ResponseEntity<LaboratorioResponseDto> create(@Valid @RequestBody LaboratorioCreateDto createDto) {
        Laboratorio laboratorio = LaboratorioMapper.toEntity(createDto);
        Laboratorio created = laboratorioService.create(laboratorio);
        LaboratorioResponseDto responseDto = LaboratorioMapper.toResponseDto(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * PUT /laboratorios/{id} - Actualizar laboratorio existente
     * @param updateDto Datos del laboratorio a actualizar (campos opcionales)
     * @return LaboratorioResponseDto del laboratorio actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<LaboratorioResponseDto> update(@PathVariable Long id, @Valid @RequestBody LaboratorioUpdateDto updateDto) {
        Laboratorio laboratorio = laboratorioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Laboratorio no encontrado con id: " + id));

        LaboratorioMapper.updateEntityFromDto(laboratorio, updateDto);
        Laboratorio updated = laboratorioService.update(id, laboratorio);
        LaboratorioResponseDto responseDto = LaboratorioMapper.toResponseDto(updated);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * DELETE /laboratorios/{id} - Eliminar laboratorio
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        laboratorioService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Laboratorio eliminado exitosamente"));
    }
}
