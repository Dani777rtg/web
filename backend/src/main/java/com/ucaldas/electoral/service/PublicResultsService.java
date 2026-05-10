package com.ucaldas.electoral.service;

import com.ucaldas.electoral.domain.EstadoPlancha;
import com.ucaldas.electoral.domain.Plancha;
import com.ucaldas.electoral.repo.PlanchaRepository;
import com.ucaldas.electoral.repo.VotoRepository;
import com.ucaldas.electoral.web.dto.VoteDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PublicResultsService {

    private final VotoRepository votoRepository;
    private final PlanchaRepository planchaRepository;

    public PublicResultsService(VotoRepository votoRepository, PlanchaRepository planchaRepository) {
        this.votoRepository = votoRepository;
        this.planchaRepository = planchaRepository;
    }

    @Transactional(readOnly = true)
    public VoteDtos.PublicResultsResponse aggregateByProceso(Long procesoId) {
        long total = votoRepository.countByProcesoId(procesoId);
        List<Object[]> rows = votoRepository.countByPlanchaForProceso(procesoId);
        Map<Long, Long> counts = rows.stream().collect(Collectors.toMap(
                r -> (Long) r[0],
                r -> (Long) r[1]
        ));

        List<Plancha> planchas = planchaRepository.findByProceso_IdAndEstado(procesoId, EstadoPlancha.APROBADA);
        List<VoteDtos.PlanchaResult> list = new ArrayList<>();
        for (Plancha p : planchas) {
            long c = counts.getOrDefault(p.getId(), 0L);
            list.add(new VoteDtos.PlanchaResult(p.getId(), p.getNombre(), c));
        }
        return new VoteDtos.PublicResultsResponse(total, list);
    }
}
