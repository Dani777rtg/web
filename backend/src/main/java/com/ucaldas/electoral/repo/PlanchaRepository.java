package com.ucaldas.electoral.repo;

import com.ucaldas.electoral.domain.EstadoPlancha;
import com.ucaldas.electoral.domain.Plancha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanchaRepository extends JpaRepository<Plancha, Long> {
    List<Plancha> findByProceso_Id(Long procesoId);

    List<Plancha> findByProceso_IdAndEstado(Long procesoId, EstadoPlancha estado);
}
