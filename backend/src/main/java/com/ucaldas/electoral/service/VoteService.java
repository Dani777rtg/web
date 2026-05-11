package com.ucaldas.electoral.service;

import com.ucaldas.electoral.domain.*;
import com.ucaldas.electoral.repo.*;
import com.ucaldas.electoral.web.dto.VoteDtos;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class VoteService {

    private final VotoRepository votoRepository;
    private final ElectoralProcessRepository processRepository;
    private final PlanchaRepository planchaRepository;
    private final AppUserRepository appUserRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public VoteService(
            VotoRepository votoRepository,
            ElectoralProcessRepository processRepository,
            PlanchaRepository planchaRepository,
            AppUserRepository appUserRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.votoRepository = votoRepository;
        this.processRepository = processRepository;
        this.planchaRepository = planchaRepository;
        this.appUserRepository = appUserRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void castVote(Long userId, VoteDtos.CastVoteRequest req) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        if (user.getRol() == Rol.ADMIN) {
            throw new AccessDeniedException("Los administradores no pueden emitir voto.");
        }
        ElectoralProcess proceso = processRepository.findById(req.procesoId())
                .orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado."));
        Plancha plancha = planchaRepository.findById(req.planchaId())
                .orElseThrow(() -> new IllegalArgumentException("Plancha no encontrada."));

        if (proceso.getEstado() != EstadoProceso.VOTACION_ABIERTA) {
            throw new IllegalStateException("La votación no está abierta.");
        }
        Instant now = Instant.now();
        if (now.isBefore(proceso.getFechaInicio()) || now.isAfter(proceso.getFechaFin())) {
            throw new IllegalStateException("Fuera del periodo de votación.");
        }
        if (!plancha.getProceso().getId().equals(proceso.getId())) {
            throw new IllegalArgumentException("La plancha no pertenece a este proceso.");
        }
        if (plancha.getEstado() != EstadoPlancha.APROBADA) {
            throw new IllegalStateException("Esta plancha no está aprobada para recibir votos.");
        }

        if (proceso.getAlcance() == AlcanceElectoral.FACULTAD) {
            if (user.getFaculty() == null || proceso.getFacultad() == null
                    || !user.getFaculty().getId().equals(proceso.getFacultad().getId())) {
                throw new IllegalStateException("No está habilitado para votar en este proceso (facultad).");
            }
        }

        if (votoRepository.findByUsuarioIdAndProcesoId(userId, proceso.getId()).isPresent()) {
            throw new IllegalStateException("Ya emitió su voto en este proceso.");
        }

        Voto voto = new Voto();
        voto.setUsuario(user);
        voto.setProceso(proceso);
        voto.setPlancha(plancha);
        votoRepository.save(voto);

        long total = votoRepository.countByProcesoId(proceso.getId());
        messagingTemplate.convertAndSend("/topic/stats/" + proceso.getId(),
                new VoteDtos.LiveStatsPayload(proceso.getId(), total));
    }
}
