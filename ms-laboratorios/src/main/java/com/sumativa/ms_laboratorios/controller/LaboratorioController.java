package com.sumativa.ms_laboratorios.controller;

import com.sumativa.ms_laboratorios.entity.Laboratorio;
import com.sumativa.ms_laboratorios.service.LaboratorioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
     */
    @GetMapping
    public ResponseEntity<List<Laboratorio>> findAll() {
        return ResponseEntity.ok(laboratorioService.findAll());
    }

    /**
     * GET /laboratorios/{id} - Obtener laboratorio por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return laboratorioService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Laboratorio no encontrado con id: " + id)));
    }

    /**
     * POST /laboratorios - Crear nuevo laboratorio
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Laboratorio laboratorio) {
        try {
            Laboratorio created = laboratorioService.create(laboratorio);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /laboratorios/{id} - Actualizar laboratorio existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Laboratorio laboratorio) {
        try {
            Laboratorio updated = laboratorioService.update(id, laboratorio);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /laboratorios/{id} - Eliminar laboratorio
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            laboratorioService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Laboratorio eliminado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
