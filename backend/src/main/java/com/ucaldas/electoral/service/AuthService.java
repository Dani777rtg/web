package com.ucaldas.electoral.service;

import com.ucaldas.electoral.config.AppProperties;
import com.ucaldas.electoral.domain.*;
import com.ucaldas.electoral.repo.AppUserRepository;
import com.ucaldas.electoral.repo.EmailVerificationRepository;
import com.ucaldas.electoral.repo.FacultyRepository;
import com.ucaldas.electoral.security.JwtService;
import com.ucaldas.electoral.security.UserPrincipal;
import com.ucaldas.electoral.web.dto.AuthDtos;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final AppUserRepository appUserRepository;
    private final FacultyRepository facultyRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final InstitutionalEmailValidator emailValidator;
    private final AppProperties appProperties;
    private final MailNotificationService mailNotificationService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            AppUserRepository appUserRepository,
            FacultyRepository facultyRepository,
            EmailVerificationRepository emailVerificationRepository,
            PasswordEncoder passwordEncoder,
            InstitutionalEmailValidator emailValidator,
            AppProperties appProperties,
            MailNotificationService mailNotificationService,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.appUserRepository = appUserRepository;
        this.facultyRepository = facultyRepository;
        this.emailVerificationRepository = emailVerificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailValidator = emailValidator;
        this.appProperties = appProperties;
        this.mailNotificationService = mailNotificationService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public void register(AuthDtos.RegisterRequest req) {
        emailValidator.validateOrThrow(req.email());
        if (appUserRepository.existsByEmailIgnoreCase(req.email())) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
        if (appUserRepository.existsByCodigoInstitucional(req.codigoInstitucional())) {
            throw new IllegalArgumentException("El código institucional ya está registrado.");
        }
        Faculty faculty = facultyRepository.findById(req.facultadId())
                .orElseThrow(() -> new IllegalArgumentException("Facultad no válida."));

        AppUser user = new AppUser();
        user.setEmail(req.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setNombreCompleto(req.nombreCompleto());
        user.setCodigoInstitucional(req.codigoInstitucional());
        user.setTipoUsuario(req.tipoUsuario());
        user.setRol(Rol.USER);
        user.setFaculty(faculty);
        user.setEmailVerificado(false);
        user.setActivo(true);
        user = appUserRepository.save(user);

        String rawCode = generateNumericCode();
        EmailVerification ev = new EmailVerification();
        ev.setUser(user);
        ev.setCodeHash(passwordEncoder.encode(rawCode));
        ev.setExpiresAt(Instant.now().plus(appProperties.getVerification().getTtlMinutes(), ChronoUnit.MINUTES));
        ev.setIntentos(0);
        emailVerificationRepository.save(ev);

        mailNotificationService.sendVerificationCode(user.getEmail(), rawCode);
    }

    @Transactional
    public AuthDtos.TokenResponse verify(AuthDtos.VerifyRequest req) {
        AppUser user = appUserRepository.findByEmailIgnoreCase(req.email().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        if (user.isEmailVerificado()) {
            throw new IllegalArgumentException("El correo ya fue verificado.");
        }
        EmailVerification ev = emailVerificationRepository.findFirstByUserIdOrderByIdDesc(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("No hay código pendiente. Solicita un nuevo registro."));

        if (ev.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("El código expiró. Regístrate de nuevo o contacta soporte.");
        }
        if (ev.getIntentos() >= appProperties.getVerification().getMaxAttempts()) {
            throw new IllegalArgumentException("Demasiados intentos fallidos.");
        }

        if (!passwordEncoder.matches(req.code(), ev.getCodeHash())) {
            ev.setIntentos(ev.getIntentos() + 1);
            emailVerificationRepository.save(ev);
            throw new IllegalArgumentException("Código incorrecto.");
        }

        user.setEmailVerificado(true);
        appUserRepository.save(user);
        emailVerificationRepository.delete(ev);

        String token = jwtService.generateToken(user);
        return new AuthDtos.TokenResponse(token, user.getEmail(), user.getRol().name());
    }

    public AuthDtos.TokenResponse login(AuthDtos.LoginRequest req) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email().trim().toLowerCase(), req.password()));
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        AppUser user = principal.getUser();
        String token = jwtService.generateToken(user);
        return new AuthDtos.TokenResponse(token, user.getEmail(), user.getRol().name());
    }

    private String generateNumericCode() {
        int len = appProperties.getVerification().getCodeLength();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
