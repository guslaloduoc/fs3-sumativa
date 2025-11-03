package com.sumativa.ms_laboratorios.controller;

import com.sumativa.ms_laboratorios.entity.Asignacion;
import com.sumativa.ms_laboratorios.service.AsignacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de asignaciones
 */
@RestController
@RequestMapping("/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionService asignacionService;

    /**
     * GET /asignaciones - Listar todas las asignaciones
     */
    @GetMapping
    public ResponseEntity<List<Asignacion>> findAll() {
        return ResponseEntity.ok(asignacionService.findAll());
    }

    /**
     * GET /asignaciones/{id} - Obtener asignación por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return asignacionService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Asignación no encontrada con id: " + id)));
    }

    /**
     * POST /asignaciones - Crear nueva asignación
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Asignacion asignacion) {
        try {
            Asignacion created = asignacionService.create(asignacion);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /asignaciones/{id} - Actualizar asignación existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Asignacion asignacion) {
        try {
            Asignacion updated = asignacionService.update(id, asignacion);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /asignaciones/{id} - Eliminar asignación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            asignacionService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Asignación eliminada exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
