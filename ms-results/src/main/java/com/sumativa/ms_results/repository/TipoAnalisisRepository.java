package com.sumativa.ms_results.repository;

import com.sumativa.ms_results.entity.TipoAnalisis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoAnalisisRepository extends JpaRepository<TipoAnalisis, Long> {

    Optional<TipoAnalisis> findByNombreIgnoreCase(String nombre);

    List<TipoAnalisis> findByCategoria(String categoria);

    List<TipoAnalisis> findByActivoTrue();
}
