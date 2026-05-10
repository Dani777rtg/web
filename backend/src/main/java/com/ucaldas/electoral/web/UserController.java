package com.ucaldas.electoral.web;

import com.ucaldas.electoral.domain.AppUser;
import com.ucaldas.electoral.security.CurrentUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/me")
@Transactional(readOnly = true)
public class UserController {

    private final CurrentUser currentUser;

    public UserController(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @GetMapping
    public Map<String, Object> me() {
        AppUser u = currentUser.requireUser();
        return Map.of(
                "id", u.getId(),
                "email", u.getEmail(),
                "nombreCompleto", u.getNombreCompleto(),
                "rol", u.getRol().name(),
                "tipoUsuario", u.getTipoUsuario().name(),
                "facultadId", u.getFaculty().getId(),
                "facultadNombre", u.getFaculty().getNombre()
        );
    }
}
