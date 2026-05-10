package com.ucaldas.electoral.repo;

import com.ucaldas.electoral.domain.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidatoRepository extends JpaRepository<Candidato, Long> {
    List<Candidato> findByPlanchaId(Long planchaId);

    List<Candidato> findByPlancha_Proceso_Id(Long procesoId);
}
