package com.sumativa.ms_laboratorios.repository;

import com.sumativa.ms_laboratorios.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Asignacion
 * Proporciona operaciones CRUD y consultas personalizadas para asignaciones.
 */
@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
}
