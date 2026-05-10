package com.ucaldas.electoral.web.dto;

import com.ucaldas.electoral.domain.AlcanceElectoral;
import com.ucaldas.electoral.domain.EstadoProceso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

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
            EstadoProceso estado
    ) {
    }

    public record ProcessResponse(
            Long id,
            String nombre,
            AlcanceElectoral alcance,
            Long facultadId,
            Long collegialBodyId,
            Instant fechaInicio,
            Instant fechaFin,
            EstadoProceso estado
    ) {
    }
}
