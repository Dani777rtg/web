package com.ucaldas.electoral.web.dto;

import jakarta.validation.constraints.NotBlank;

public final class FacultyDtos {
    private FacultyDtos() {
    }

    public record FacultyResponse(Long id, String nombre) {
    }

    public record FacultyCreateRequest(@NotBlank String nombre) {
    }
}
