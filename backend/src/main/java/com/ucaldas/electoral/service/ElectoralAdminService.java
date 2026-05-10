package com.ucaldas.electoral.service;

import com.ucaldas.electoral.domain.*;
import com.ucaldas.electoral.repo.*;
import com.ucaldas.electoral.web.dto.ProcessDtos;
import com.ucaldas.electoral.web.dto.PlanchaDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ElectoralAdminService {

    private final ElectoralProcessRepository processRepository;
    private final FacultyRepository facultyRepository;
    private final CollegialBodyRepository collegialBodyRepository;
    private final PlanchaRepository planchaRepository;
    private final CandidatoRepository candidatoRepository;
    private final AppUserRepository appUserRepository;

    public ElectoralAdminService(
            ElectoralProcessRepository processRepository,
            FacultyRepository facultyRepository,
            CollegialBodyRepository collegialBodyRepository,
            PlanchaRepository planchaRepository,
            CandidatoRepository candidatoRepository,
            AppUserRepository appUserRepository
    ) {
        this.processRepository = processRepository;
        this.facultyRepository = facultyRepository;
        this.collegialBodyRepository = collegialBodyRepository;
        this.planchaRepository = planchaRepository;
        this.candidatoRepository = candidatoRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public ProcessDtos.ProcessResponse createProcess(ProcessDtos.CreateProcessRequest req) {
        ElectoralProcess p = new ElectoralProcess();
        p.setNombre(req.nombre());
        p.setAlcance(req.alcance());
        p.setFechaInicio(req.fechaInicio());
        p.setFechaFin(req.fechaFin());
        p.setEstado(req.estado() != null ? req.estado() : EstadoProceso.BORRADOR);

        if (req.alcance() == AlcanceElectoral.FACULTAD) {
            Faculty f = facultyRepository.findById(req.facultadId())
                    .orElseThrow(() -> new IllegalArgumentException("Facultad requerida para alcance FACULTAD."));
            p.setFacultad(f);
        }
        if (req.collegialBodyId() != null) {
            CollegialBody cb = collegialBodyRepository.findById(req.collegialBodyId())
                    .orElseThrow(() -> new IllegalArgumentException("Cuerpo colegiado no válido."));
            p.setCollegialBody(cb);
        }
        ElectoralProcess saved = processRepository.save(p);
        return toProcessResponse(saved);
    }

    @Transactional
    public ProcessDtos.ProcessResponse updateProcessState(Long id, EstadoProceso estado) {
        ElectoralProcess p = processRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado."));
        p.setEstado(estado);
        return toProcessResponse(processRepository.save(p));
    }

    @Transactional
    public PlanchaDtos.PlanchaResponse createPlanchaWithCandidates(PlanchaDtos.CreatePlanchaRequest req) {
        ElectoralProcess proceso = processRepository.findById(req.procesoId())
                .orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado."));

        Plancha plancha = new Plancha();
        plancha.setNombre(req.nombre());
        plancha.setDescripcion(req.descripcion());
        plancha.setEstado(req.estadoInicial() != null ? req.estadoInicial() : EstadoPlancha.BORRADOR);
        plancha.setProceso(proceso);
        if (req.collegialBodyId() != null) {
            plancha.setCollegialBody(collegialBodyRepository.findById(req.collegialBodyId()).orElseThrow());
        }
        if (req.facultadId() != null) {
            plancha.setFacultad(facultyRepository.findById(req.facultadId()).orElseThrow());
        }
        plancha = planchaRepository.save(plancha);

        if (req.candidatoPrincipalUserId() != null) {
            AppUser u = appUserRepository.findById(req.candidatoPrincipalUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario principal no encontrado."));
            Candidato c = new Candidato();
            c.setPlancha(plancha);
            c.setUsuario(u);
            c.setRol(RolCandidato.PRINCIPAL);
            candidatoRepository.save(c);
        }
        if (req.candidatoSuplenteUserId() != null) {
            AppUser u = appUserRepository.findById(req.candidatoSuplenteUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario suplente no encontrado."));
            Candidato c = new Candidato();
            c.setPlancha(plancha);
            c.setUsuario(u);
            c.setRol(RolCandidato.SUPLENTE);
            candidatoRepository.save(c);
        }

        return toPlanchaResponse(plancha);
    }

    @Transactional
    public PlanchaDtos.PlanchaResponse updatePlanchaEstado(Long planchaId, EstadoPlancha estado) {
        Plancha p = planchaRepository.findById(planchaId)
                .orElseThrow(() -> new IllegalArgumentException("Plancha no encontrada."));
        p.setEstado(estado);
        return toPlanchaResponse(planchaRepository.save(p));
    }

    private ProcessDtos.ProcessResponse toProcessResponse(ElectoralProcess p) {
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

    private PlanchaDtos.PlanchaResponse toPlanchaResponse(Plancha p) {
        return new PlanchaDtos.PlanchaResponse(
                p.getId(),
                p.getNombre(),
                p.getEstado(),
                p.getProceso().getId()
        );
    }
}
