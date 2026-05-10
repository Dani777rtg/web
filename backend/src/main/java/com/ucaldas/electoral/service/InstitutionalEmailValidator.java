package com.ucaldas.electoral.service;

import com.ucaldas.electoral.config.AppProperties;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class InstitutionalEmailValidator {

    private final AppProperties appProperties;

    public InstitutionalEmailValidator(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public boolean isAllowed(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        String lower = email.trim().toLowerCase(Locale.ROOT);
        int at = lower.lastIndexOf('@');
        if (at < 1 || at == lower.length() - 1) {
            return false;
        }
        String domain = lower.substring(at + 1);

        for (String suffix : appProperties.getEmail().getAllowedDomainSuffixes()) {
            String s = suffix.startsWith("@") ? suffix.substring(1) : suffix;
            if (domain.equals(s)) {
                return true;
            }
        }

        if (appProperties.getEmail().isAllowUcaldasSubdomains()) {
            if (domain.endsWith(".ucaldas.edu.co") || domain.endsWith("ucaldas.edu.co")) {
                return true;
            }
            return domain.endsWith(".ucaldas.co") || domain.equals("ucaldas.co");
        }
        return false;
    }

    public void validateOrThrow(String email) {
        if (!isAllowed(email)) {
            throw new IllegalArgumentException("El correo no pertenece a un dominio institucional permitido.");
        }
    }
}
