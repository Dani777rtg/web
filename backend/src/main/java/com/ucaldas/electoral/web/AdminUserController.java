package com.ucaldas.electoral.web;

import com.ucaldas.electoral.repo.AppUserRepository;
import com.ucaldas.electoral.web.dto.UserListDtos;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@Transactional(readOnly = true)
public class AdminUserController {

    private final AppUserRepository appUserRepository;

    public AdminUserController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @GetMapping
    public List<UserListDtos.UserSummary> list() {
        return appUserRepository.findAll().stream()
                .map(u -> new UserListDtos.UserSummary(
                        u.getId(),
                        u.getEmail(),
                        u.getNombreCompleto(),
                        u.getRol().name(),
                        u.getTipoUsuario().name(),
                        u.getFaculty().getId()
                ))
                .toList();
    }
}
