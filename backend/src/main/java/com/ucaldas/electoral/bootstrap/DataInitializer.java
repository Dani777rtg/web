package com.ucaldas.electoral.bootstrap;

import com.ucaldas.electoral.domain.AppUser;
import com.ucaldas.electoral.domain.Faculty;
import com.ucaldas.electoral.domain.Rol;
import com.ucaldas.electoral.domain.TipoUsuario;
import com.ucaldas.electoral.repo.AppUserRepository;
import com.ucaldas.electoral.repo.FacultyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AppUserRepository appUserRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            AppUserRepository appUserRepository,
            FacultyRepository facultyRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.appUserRepository = appUserRepository;
        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (appUserRepository.existsByEmailIgnoreCase("admin@ucaldas.edu.co")) {
            return;
        }
        Faculty first = facultyRepository.findByNombre("Inteligencia Artificial e Ingenierías")
                .or(() -> facultyRepository.findAll().stream().findFirst())
                .orElseThrow(() -> new IllegalStateException("No hay facultades en BD. Revise migraciones."));
        AppUser admin = new AppUser();
        admin.setEmail("admin@ucaldas.edu.co");
        admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
        admin.setNombreCompleto("Administrador Sistema");
        admin.setCodigoInstitucional("ADMIN-1");
        admin.setTipoUsuario(TipoUsuario.PROFESOR);
        admin.setRol(Rol.ADMIN);
        admin.setFaculty(first);
        admin.setEmailVerificado(true);
        admin.setActivo(true);
        appUserRepository.save(admin);
        log.info("Usuario admin creado: admin@ucaldas.edu.co / Admin123!");
    }
}
