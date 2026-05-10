package com.ucaldas.electoral.repo;

import com.ucaldas.electoral.domain.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {
    Optional<Voto> findByUsuarioIdAndProcesoId(Long usuarioId, Long procesoId);

    long countByProcesoId(Long procesoId);

    @Query("SELECT v.plancha.id, COUNT(v) FROM Voto v WHERE v.proceso.id = :procesoId GROUP BY v.plancha.id")
    List<Object[]> countByPlanchaForProceso(@Param("procesoId") Long procesoId);

    List<Voto> findByProceso_Id(Long procesoId);
}
