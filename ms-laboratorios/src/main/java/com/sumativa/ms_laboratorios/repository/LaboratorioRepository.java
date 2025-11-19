package com.sumativa.ms_laboratorios.repository;

import com.sumativa.ms_laboratorios.entity.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Laboratorio
 * Proporciona operaciones CRUD y consultas personalizadas para laboratorios.
 */
@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {

    /**
     * Busca un laboratorio por nombre (ignorando mayúsculas/minúsculas)
     * @param nombre Nombre del laboratorio
     * @return Optional con el laboratorio si existe
     */
    Optional<Laboratorio> findByNombreIgnoreCase(String nombre);
}
