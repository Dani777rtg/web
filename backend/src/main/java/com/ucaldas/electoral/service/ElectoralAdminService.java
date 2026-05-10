package com.ucaldas.electoral.service;

import com.ucaldas.electoral.domain.*;
import com.ucaldas.electoral.repo.*;
import com.ucaldas.electoral.web.dto.ProcessDtos;
import com.ucaldas.electoral.web.dto.PlanchaDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        p.setPuesto(req.puesto());
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

    /**
     * Un solo paso administrativo: proceso en borrador con candidatos ya inscritos.
     * El inicio del periodo es el momento de creación; el admin abre la votación cuando corresponda.
     */
    @Transactional
    public ProcessDtos.ProcessResponse createElection(ProcessDtos.CreateElectionRequest req) {
        Instant now = Instant.now();
        if (!req.fechaFin().isAfter(now)) {
            throw new IllegalArgumentException("La fecha y hora de cierre debe ser posterior al momento actual.");
        }
        if (req.alcance() == AlcanceElectoral.FACULTAD && req.facultadId() == null) {
            throw new IllegalArgumentException("facultadId es obligatorio para alcance FACULTAD.");
        }
        String puesto = req.puesto().trim();
        if (puesto.isEmpty()) {
            throw new IllegalArgumentException("Indique el puesto o cargo en disputa.");
        }
        ProcessDtos.ProcessResponse created = createProcess(new ProcessDtos.CreateProcessRequest(
                req.nombre().trim(),
                req.alcance(),
                req.alcance() == AlcanceElectoral.FACULTAD ? req.facultadId() : null,
                null,
                now,
                req.fechaFin(),
                EstadoProceso.BORRADOR,
                puesto
        ));
        addCandidatesFromUsers(created.id(), req.candidateUserIds());
        return processRepository.findById(created.id()).map(this::toProcessResponse).orElseThrow();
    }

    @Transactional
    public ProcessDtos.ProcessResponse updateProcessState(Long id, EstadoProceso estado) {
        ElectoralProcess p = processRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado."));
        p.setEstado(estado);
        return toProcessResponse(processRepository.save(p));
    }

    /**
     * Inscribe varios usuarios como candidatos del mismo proceso: cada uno es una opción en la urna
     * (internamente una plancha aprobada con candidato PRINCIPAL).
     */
    @Transactional
    public List<PlanchaDtos.PlanchaResponse> addCandidatesFromUsers(Long procesoId, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("Indique al menos un candidato.");
        }
        ElectoralProcess proceso = processRepository.findById(procesoId)
                .orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado."));

        Set<Long> yaInscritos = candidatoRepository.findByPlancha_Proceso_Id(procesoId).stream()
                .filter(c -> c.getRol() == RolCandidato.PRINCIPAL)
                .map(c -> c.getUsuario().getId())
                .collect(Collectors.toSet());

        List<PlanchaDtos.PlanchaResponse> creadas = new ArrayList<>();
        List<Long> unicos = userIds.stream().distinct().toList();
        for (Long uid : unicos) {
            if (yaInscritos.contains(uid)) {
                continue;
            }
            AppUser u = appUserRepository.findById(uid)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + uid));

            if (proceso.getAlcance() == AlcanceElectoral.FACULTAD) {
                if (u.getFaculty() == null || proceso.getFacultad() == null
                        || !u.getFaculty().getId().equals(proceso.getFacultad().getId())) {
                    throw new IllegalArgumentException(
                            "El candidato «" + u.getNombreCompleto() + "» no pertenece a la facultad de este proceso.");
                }
            }

            Plancha plancha = new Plancha();
            String nombre = u.getNombreCompleto();
            if (nombre.length() > 500) {
                nombre = nombre.substring(0, 500);
            }
            plancha.setNombre(nombre);
            plancha.setEstado(EstadoPlancha.APROBADA);
            plancha.setProceso(proceso);
            plancha.setCollegialBody(proceso.getCollegialBody());
            plancha.setFacultad(proceso.getFacultad());
            plancha = planchaRepository.save(plancha);

            Candidato c = new Candidato();
            c.setPlancha(plancha);
            c.setUsuario(u);
            c.setRol(RolCandidato.PRINCIPAL);
            candidatoRepository.save(c);

            yaInscritos.add(uid);
            creadas.add(toPlanchaResponse(plancha));
        }
        return creadas;
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
                p.getPuesto() != null ? p.getPuesto() : "",
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
