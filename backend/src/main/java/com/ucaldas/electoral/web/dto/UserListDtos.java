package com.ucaldas.electoral.web.dto;

public final class UserListDtos {
    private UserListDtos() {
    }

    public record UserSummary(Long id, String email, String nombreCompleto, String rol, String tipoUsuario, Long facultadId) {
    }
}
