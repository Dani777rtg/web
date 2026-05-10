package com.ucaldas.electoral.web.dto;

import com.ucaldas.electoral.domain.AlcanceElectoral;
import com.ucaldas.electoral.domain.EstadoProceso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public final class ProcessDtos {
    private ProcessDtos() {
    }

    public record CreateProcessRequest(
            @NotBlank String nombre,
            @NotNull AlcanceElectoral alcance,
            Long facultadId,
            Long collegialBodyId,
            @NotNull Instant fechaInicio,
            @NotNull Instant fechaFin,
            EstadoProceso estado,
            String puesto
    ) {
        public CreateProcessRequest {
            if (puesto == null) {
                puesto = "";
            } else {
                puesto = puesto.trim();
            }
        }
    }

    /**
     * Creación simplificada: nombre, puesto, alcance, cierre, candidatos (usuarios).
     * La votación queda en borrador hasta que el admin pulse «Abrir votación».
     */
    public record CreateElectionRequest(
            @NotBlank String nombre,
            @NotBlank String puesto,
            @NotNull AlcanceElectoral alcance,
            Long facultadId,
            @NotNull Instant fechaFin,
            @NotEmpty List<Long> candidateUserIds
    ) {
    }

    public record ProcessResponse(
            Long id,
            String nombre,
            String puesto,
            AlcanceElectoral alcance,
            Long facultadId,
            Long collegialBodyId,
            Instant fechaInicio,
            Instant fechaFin,
            EstadoProceso estado
    ) {
    }

    /** Usuarios registrados que competirán: cada uno genera una opción de voto (plancha aprobada con un titular). */
    public record AddCandidatesRequest(@NotEmpty List<Long> userIds) {
    }
}
