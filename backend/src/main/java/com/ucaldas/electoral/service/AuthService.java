package com.ucaldas.electoral.service;

import com.ucaldas.electoral.domain.*;
import com.ucaldas.electoral.repo.AppUserRepository;
import com.ucaldas.electoral.repo.FacultyRepository;
import com.ucaldas.electoral.security.JwtService;
import com.ucaldas.electoral.security.UserPrincipal;
import com.ucaldas.electoral.web.dto.AuthDtos;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;
    private final InstitutionalEmailValidator emailValidator;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            AppUserRepository appUserRepository,
            FacultyRepository facultyRepository,
            PasswordEncoder passwordEncoder,
            InstitutionalEmailValidator emailValidator,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.appUserRepository = appUserRepository;
        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailValidator = emailValidator;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthDtos.TokenResponse register(AuthDtos.RegisterRequest req) {
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
        user.setEmailVerificado(true);
        user.setActivo(true);
        user = appUserRepository.save(user);

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
}
