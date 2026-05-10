package com.ucaldas.electoral.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final Email email = new Email();

    public Jwt getJwt() {
        return jwt;
    }

    public Email getEmail() {
        return email;
    }

    public static class Jwt {
        private String secret = "dev-secret-change-in-production-min-256-bits-required-for-hs512-xxxxxxxx";
        private long expirationMs = 86400000L;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpirationMs() {
            return expirationMs;
        }

        public void setExpirationMs(long expirationMs) {
            this.expirationMs = expirationMs;
        }
    }

    public static class Email {
        private List<String> allowedDomainSuffixes = new ArrayList<>();
        private boolean allowUcaldasSubdomains = true;

        public List<String> getAllowedDomainSuffixes() {
            return allowedDomainSuffixes;
        }

        public void setAllowedDomainSuffixes(List<String> allowedDomainSuffixes) {
            this.allowedDomainSuffixes = allowedDomainSuffixes;
        }

        public boolean isAllowUcaldasSubdomains() {
            return allowUcaldasSubdomains;
        }

        public void setAllowUcaldasSubdomains(boolean allowUcaldasSubdomains) {
            this.allowUcaldasSubdomains = allowUcaldasSubdomains;
        }
    }
}
