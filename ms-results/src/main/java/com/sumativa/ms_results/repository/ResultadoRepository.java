package com.sumativa.ms_results.repository;

import com.sumativa.ms_results.entity.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultadoRepository extends JpaRepository<Resultado, Long> {

    List<Resultado> findByPacienteContaining(String paciente);

    List<Resultado> findByLaboratorioId(Long laboratorioId);

    List<Resultado> findByEstado(String estado);
}
