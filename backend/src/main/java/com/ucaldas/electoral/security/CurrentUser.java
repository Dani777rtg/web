package com.ucaldas.electoral.security;

import com.ucaldas.electoral.domain.AppUser;
import com.ucaldas.electoral.repo.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    private final AppUserRepository appUserRepository;

    public CurrentUser(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser requireUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new IllegalStateException("No autenticado.");
        }
        UserPrincipal p = (UserPrincipal) auth.getPrincipal();
        return appUserRepository.findById(p.getUser().getId()).orElseThrow();
    }

    public Long requireUserId() {
        return requireUser().getId();
    }
}
