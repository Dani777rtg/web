package com.ucaldas.electoral.web.dto;

import jakarta.validation.constraints.NotNull;

public final class VoteDtos {
    private VoteDtos() {
    }

    public record CastVoteRequest(
            @NotNull Long procesoId,
            @NotNull Long planchaId
    ) {
    }

    public record LiveStatsPayload(Long procesoId, long votosEmitidos) {
    }

    public record PlanchaResult(Long planchaId, String nombre, long votos) {
    }

    public record PublicResultsResponse(long votosTotales, java.util.List<PlanchaResult> porPlancha) {
    }

    public record AdminVoteRow(Long votoId, Long usuarioId, String email, String nombreCompleto,
                               Long planchaId, String planchaNombre, String fecha) {
    }
}
