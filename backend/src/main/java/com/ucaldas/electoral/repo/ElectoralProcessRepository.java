package com.ucaldas.electoral.repo;

import com.ucaldas.electoral.domain.ElectoralProcess;
import com.ucaldas.electoral.domain.EstadoProceso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElectoralProcessRepository extends JpaRepository<ElectoralProcess, Long> {
    List<ElectoralProcess> findByEstado(EstadoProceso estado);
}
