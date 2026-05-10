package com.ucaldas.electoral.web;

import com.ucaldas.electoral.domain.AlcanceElectoral;
import com.ucaldas.electoral.domain.EstadoPlancha;
import com.ucaldas.electoral.domain.EstadoProceso;
import com.ucaldas.electoral.repo.ElectoralProcessRepository;
import com.ucaldas.electoral.service.ElectoralAdminService;
import com.ucaldas.electoral.web.dto.PlanchaDtos;
import com.ucaldas.electoral.web.dto.ProcessDtos;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Transactional(readOnly = true)
public class AdminElectoralController {

    private final ElectoralAdminService electoralAdminService;
    private final ElectoralProcessRepository processRepository;

    public AdminElectoralController(
            ElectoralAdminService electoralAdminService,
            ElectoralProcessRepository processRepository
    ) {
        this.electoralAdminService = electoralAdminService;
        this.processRepository = processRepository;
    }

    @PostMapping("/processes")
    @Transactional
    public ProcessDtos.ProcessResponse createProcess(@Valid @RequestBody ProcessDtos.CreateProcessRequest req) {
        if (req.alcance() == AlcanceElectoral.FACULTAD && req.facultadId() == null) {
            throw new IllegalArgumentException("facultadId es obligatorio para alcance FACULTAD.");
        }
        return electoralAdminService.createProcess(req);
    }

    @PatchMapping("/processes/{id}/estado")
    @Transactional
    public ProcessDtos.ProcessResponse estado(@PathVariable Long id, @RequestParam EstadoProceso estado) {
        return electoralAdminService.updateProcessState(id, estado);
    }

    @GetMapping("/processes")
    public List<ProcessDtos.ProcessResponse> listProcesses() {
        return processRepository.findAll().stream().map(p -> new ProcessDtos.ProcessResponse(
                p.getId(),
                p.getNombre(),
                p.getAlcance(),
                p.getFacultad() != null ? p.getFacultad().getId() : null,
                p.getCollegialBody() != null ? p.getCollegialBody().getId() : null,
                p.getFechaInicio(),
                p.getFechaFin(),
                p.getEstado()
        )).collect(Collectors.toList());
    }

    @PostMapping("/planchas")
    @Transactional
    public PlanchaDtos.PlanchaResponse createPlancha(@Valid @RequestBody PlanchaDtos.CreatePlanchaRequest req) {
        return electoralAdminService.createPlanchaWithCandidates(req);
    }

    @PatchMapping("/planchas/{id}/estado")
    @Transactional
    public PlanchaDtos.PlanchaResponse planchaEstado(@PathVariable Long id, @RequestParam EstadoPlancha estado) {
        return electoralAdminService.updatePlanchaEstado(id, estado);
    }
}
