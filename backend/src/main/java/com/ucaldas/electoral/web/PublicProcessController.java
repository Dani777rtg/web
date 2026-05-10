package com.ucaldas.electoral.web;

import com.ucaldas.electoral.domain.Candidato;
import com.ucaldas.electoral.domain.ElectoralProcess;
import com.ucaldas.electoral.domain.EstadoPlancha;
import com.ucaldas.electoral.domain.EstadoProceso;
import com.ucaldas.electoral.domain.Plancha;
import com.ucaldas.electoral.domain.RolCandidato;
import com.ucaldas.electoral.repo.CandidatoRepository;
import com.ucaldas.electoral.repo.ElectoralProcessRepository;
import com.ucaldas.electoral.repo.PlanchaRepository;
import com.ucaldas.electoral.service.PublicResultsService;
import com.ucaldas.electoral.web.dto.PlanchaDtos;
import com.ucaldas.electoral.web.dto.ProcessDtos;
import com.ucaldas.electoral.web.dto.VoteDtos;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
@Transactional(readOnly = true)
public class PublicProcessController {

    private final ElectoralProcessRepository processRepository;
    private final PlanchaRepository planchaRepository;
    private final CandidatoRepository candidatoRepository;
    private final PublicResultsService publicResultsService;

    public PublicProcessController(
            ElectoralProcessRepository processRepository,
            PlanchaRepository planchaRepository,
            CandidatoRepository candidatoRepository,
            PublicResultsService publicResultsService
    ) {
        this.processRepository = processRepository;
        this.planchaRepository = planchaRepository;
        this.candidatoRepository = candidatoRepository;
        this.publicResultsService = publicResultsService;
    }

    @GetMapping("/processes/open")
    public List<ProcessDtos.ProcessResponse> openVoting() {
        return processRepository.findByEstado(EstadoProceso.VOTACION_ABIERTA).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/processes/{id}/planchas")
    public List<PlanchaDtos.PlanchaPublicView> planchasAprobadas(@PathVariable Long id) {
        List<Plancha> list = planchaRepository.findByProceso_IdAndEstado(id, EstadoPlancha.APROBADA);
        return list.stream().map(this::toPublicView).toList();
    }

    @GetMapping("/processes/{id}/results")
    public VoteDtos.PublicResultsResponse results(@PathVariable Long id) {
        processRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado."));
        return publicResultsService.aggregateByProceso(id);
    }

    @GetMapping("/processes/{id}/stats")
    public Map<String, Object> stats(@PathVariable Long id) {
        var agg = publicResultsService.aggregateByProceso(id);
        return Map.of(
                "procesoId", id,
                "votosEmitidos", agg.votosTotales()
        );
    }

    private ProcessDtos.ProcessResponse toResponse(ElectoralProcess p) {
        return new ProcessDtos.ProcessResponse(
                p.getId(),
                p.getNombre(),
                p.getAlcance(),
                p.getFacultad() != null ? p.getFacultad().getId() : null,
                p.getCollegialBody() != null ? p.getCollegialBody().getId() : null,
                p.getFechaInicio(),
                p.getFechaFin(),
                p.getEstado()
        );
    }

    private PlanchaDtos.PlanchaPublicView toPublicView(Plancha p) {
        List<Candidato> cands = candidatoRepository.findByPlanchaId(p.getId());
        Map<RolCandidato, String> names = cands.stream().collect(Collectors.toMap(
                Candidato::getRol,
                c -> c.getUsuario().getNombreCompleto(),
                (a, b) -> a
        ));
        return new PlanchaDtos.PlanchaPublicView(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                Optional.ofNullable(names.get(RolCandidato.PRINCIPAL)).orElse("—"),
                Optional.ofNullable(names.get(RolCandidato.SUPLENTE)).orElse("—")
        );
    }
}
