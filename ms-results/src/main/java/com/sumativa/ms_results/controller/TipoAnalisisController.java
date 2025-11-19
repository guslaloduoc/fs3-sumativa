package com.sumativa.ms_results.controller;

import com.sumativa.ms_results.entity.TipoAnalisis;
import com.sumativa.ms_results.service.TipoAnalisisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tipos-analisis")
@RequiredArgsConstructor
public class TipoAnalisisController {

    private final TipoAnalisisService tipoAnalisisService;

    @GetMapping
    public ResponseEntity<List<TipoAnalisis>> getAll() {
        List<TipoAnalisis> tipos = tipoAnalisisService.findAll();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            TipoAnalisis tipoAnalisis = tipoAnalisisService.findById(id);
            return ResponseEntity.ok(tipoAnalisis);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TipoAnalisis tipoAnalisis) {
        try {
            TipoAnalisis created = tipoAnalisisService.create(tipoAnalisis);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody TipoAnalisis tipoAnalisis) {
        try {
            TipoAnalisis updated = tipoAnalisisService.update(id, tipoAnalisis);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            tipoAnalisisService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
