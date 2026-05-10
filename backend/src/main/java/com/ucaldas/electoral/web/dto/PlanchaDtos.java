package com.ucaldas.electoral.web.dto;

import com.ucaldas.electoral.domain.EstadoPlancha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class PlanchaDtos {
    private PlanchaDtos() {
    }

    public record CreatePlanchaRequest(
            @NotNull Long procesoId,
            @NotBlank String nombre,
            String descripcion,
            Long collegialBodyId,
            Long facultadId,
            EstadoPlancha estadoInicial,
            Long candidatoPrincipalUserId,
            Long candidatoSuplenteUserId
    ) {
    }

    public record PlanchaResponse(Long id, String nombre, EstadoPlancha estado, Long procesoId) {
    }

    public record PlanchaPublicView(
            Long id,
            String nombre,
            String descripcion,
            String principalNombre,
            String suplenteNombre
    ) {
    }
}
