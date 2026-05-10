package com.ucaldas.electoral.web.dto;

import com.ucaldas.electoral.domain.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank @Email String email,
            @NotBlank String password,
            @NotBlank String nombreCompleto,
            @NotBlank String codigoInstitucional,
            @NotNull TipoUsuario tipoUsuario,
            @NotNull Long facultadId
    ) {
    }

    public record VerifyRequest(
            @NotBlank @Email String email,
            @NotBlank String code
    ) {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    public record TokenResponse(String token, String email, String rol) {
    }
}
