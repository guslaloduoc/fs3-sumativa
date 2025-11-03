package com.sumativa.ms_laboratorios.config;

import com.sumativa.ms_laboratorios.entity.Asignacion;
import com.sumativa.ms_laboratorios.entity.Laboratorio;
import com.sumativa.ms_laboratorios.repository.AsignacionRepository;
import com.sumativa.ms_laboratorios.repository.LaboratorioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Inicializador de datos para pruebas
 * Carga datos de ejemplo en la base de datos al iniciar la aplicación
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final LaboratorioRepository laboratorioRepository;
    private final AsignacionRepository asignacionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Solo inicializar si no hay datos
        if (laboratorioRepository.count() > 0) {
            log.info("La base de datos ya contiene datos. Omitiendo inicialización.");
            return;
        }

        log.info("Iniciando carga de datos de prueba...");

        // Crear laboratorios
        Laboratorio lab1 = new Laboratorio(null, "Laboratorio Central", "Av. Providencia 1234, Santiago", "+56912345678");
        Laboratorio lab2 = new Laboratorio(null, "Laboratorio Clínico San José", "Calle Principal 567, Viña del Mar", "+56987654321");
        Laboratorio lab3 = new Laboratorio(null, "Laboratorio Médico La Serena", "Av. Francisco de Aguirre 890, La Serena", "+56955555555");

        lab1 = laboratorioRepository.save(lab1);
        lab2 = laboratorioRepository.save(lab2);
        lab3 = laboratorioRepository.save(lab3);

        log.info("✓ Creados 3 laboratorios");

        // Crear asignaciones
        Asignacion asig1 = new Asignacion(null, "Juan Pérez González", LocalDate.of(2025, 11, 5), lab1);
        Asignacion asig2 = new Asignacion(null, "María García López", LocalDate.of(2025, 11, 6), lab1);
        Asignacion asig3 = new Asignacion(null, "Carlos Rodríguez Muñoz", LocalDate.of(2025, 11, 7), lab2);
        Asignacion asig4 = new Asignacion(null, "Ana Martínez Silva", LocalDate.of(2025, 11, 8), lab2);
        Asignacion asig5 = new Asignacion(null, "Pedro Sánchez Torres", LocalDate.of(2025, 11, 9), lab3);

        asignacionRepository.save(asig1);
        asignacionRepository.save(asig2);
        asignacionRepository.save(asig3);
        asignacionRepository.save(asig4);
        asignacionRepository.save(asig5);

        log.info("✓ Creadas 5 asignaciones");
        log.info("Datos de prueba cargados exitosamente.");
    }
}
