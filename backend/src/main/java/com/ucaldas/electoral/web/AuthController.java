package com.ucaldas.electoral.web;

import com.ucaldas.electoral.service.AuthService;
import com.ucaldas.electoral.web.dto.AuthDtos;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody AuthDtos.RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok(Map.of("message", "Revise su correo para el código de verificación (válido 5 minutos)."));
    }

    @PostMapping("/verify")
    public AuthDtos.TokenResponse verify(@Valid @RequestBody AuthDtos.VerifyRequest req) {
        return authService.verify(req);
    }

    @PostMapping("/login")
    public AuthDtos.TokenResponse login(@Valid @RequestBody AuthDtos.LoginRequest req) {
        return authService.login(req);
    }
}
