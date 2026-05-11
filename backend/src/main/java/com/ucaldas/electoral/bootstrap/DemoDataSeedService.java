package com.ucaldas.electoral.bootstrap;

import com.ucaldas.electoral.domain.*;
import com.ucaldas.electoral.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Datos sintéticos para pruebas locales (Docker / perfil local). Desactivar en producción real.
 * Control: {@code app.demo-seed.enabled=true} (ver {@code application-docker.yml} / {@code application-local.yml}).
 */
@Service
public class DemoDataSeedService {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeedService.class);

    public static final String DEMO_PASSWORD = "DemoVoto2026!";
    public static final String PROCESS_DIRECTOR = "DEMO — Directorio universitario";
    public static final String PROCESS_REP = "DEMO — Representante estudiantil (IA)";
    private static final String FACULTAD_IA = "Inteligencia Artificial e Ingenierías";
    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");

    private final ElectoralProcessRepository processRepository;
    private final FacultyRepository facultyRepository;
    private final AppUserRepository appUserRepository;
    private final PlanchaRepository planchaRepository;
    private final CandidatoRepository candidatoRepository;
    private final VotoRepository votoRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeedService(
            ElectoralProcessRepository processRepository,
            FacultyRepository facultyRepository,
            AppUserRepository appUserRepository,
            PlanchaRepository planchaRepository,
            CandidatoRepository candidatoRepository,
            VotoRepository votoRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.processRepository = processRepository;
        this.facultyRepository = facultyRepository;
        this.appUserRepository = appUserRepository;
        this.planchaRepository = planchaRepository;
        this.candidatoRepository = candidatoRepository;
        this.votoRepository = votoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void seedIfNeeded() {
        if (processRepository.existsByNombre(PROCESS_DIRECTOR)) {
            return;
        }
        if (appUserRepository.existsByEmailIgnoreCase("demo-voto-01@ucaldas.edu.co")) {
            log.warn("Omitiendo demo seed: existe demo-voto-01 pero no el proceso «{}». Revise la base o borre usuarios demo.", PROCESS_DIRECTOR);
            return;
        }

        Faculty ia = facultyRepository.findByNombre(FACULTAD_IA)
                .orElseThrow(() -> new IllegalStateException("Falta facultad «" + FACULTAD_IA + "» en BD."));

        Instant fechaInicio = Instant.now().minusSeconds(3_600);
        Instant fechaFin = nextSundayEndOfDayBogota();

        String hash = passwordEncoder.encode(DEMO_PASSWORD);
        List<AppUser> voters = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            String n = String.format("%02d", i);
            String email = "demo-voto-" + n + "@ucaldas.edu.co";
            AppUser u = new AppUser();
            u.setEmail(email);
            u.setPasswordHash(hash);
            u.setNombreCompleto("Estudiante demo " + n);
            u.setCodigoInstitucional("DEMO-IA-" + n);
            u.setTipoUsuario(TipoUsuario.ESTUDIANTE);
            u.setRol(Rol.USER);
            u.setFaculty(ia);
            u.setEmailVerificado(true);
            u.setActivo(true);
            voters.add(appUserRepository.save(u));
        }

        ElectoralProcess dir = new ElectoralProcess();
        dir.setNombre(PROCESS_DIRECTOR);
        dir.setPuesto("Director/a representante ante el Consejo Superior Universitario");
        dir.setAlcance(AlcanceElectoral.UNIVERSIDAD);
        dir.setFacultad(null);
        dir.setCollegialBody(null);
        dir.setFechaInicio(fechaInicio);
        dir.setFechaFin(fechaFin);
        dir.setEstado(EstadoProceso.VOTACION_ABIERTA);
        dir = processRepository.save(dir);

        AppUser c1 = voters.get(0);
        AppUser c2 = voters.get(1);
        AppUser c3 = voters.get(2);
        Plancha pDir1 = savePlancha(dir, null, null, c1.getNombreCompleto(), c1);
        Plancha pDir2 = savePlancha(dir, null, null, c2.getNombreCompleto(), c2);
        Plancha pDir3 = savePlancha(dir, null, null, c3.getNombreCompleto(), c3);

        castVote(voters.get(2), dir, pDir1);
        for (int i = 3; i <= 12; i++) {
            castVote(voters.get(i), dir, pDir1);
        }
        castVote(voters.get(0), dir, pDir2);
        for (int i = 13; i <= 22; i++) {
            castVote(voters.get(i), dir, pDir2);
        }
        castVote(voters.get(1), dir, pDir3);
        for (int i = 23; i <= 29; i++) {
            castVote(voters.get(i), dir, pDir3);
        }

        ElectoralProcess rep = new ElectoralProcess();
        rep.setNombre(PROCESS_REP);
        rep.setPuesto("Representante estudiantil");
        rep.setAlcance(AlcanceElectoral.FACULTAD);
        rep.setFacultad(ia);
        rep.setCollegialBody(null);
        rep.setFechaInicio(fechaInicio);
        rep.setFechaFin(fechaFin);
        rep.setEstado(EstadoProceso.VOTACION_ABIERTA);
        rep = processRepository.save(rep);

        AppUser r1 = voters.get(3);
        AppUser r2 = voters.get(4);
        AppUser r3 = voters.get(5);
        Plancha pRep1 = savePlancha(rep, ia, null, r1.getNombreCompleto(), r1);
        Plancha pRep2 = savePlancha(rep, ia, null, r2.getNombreCompleto(), r2);
        Plancha pRep3 = savePlancha(rep, ia, null, r3.getNombreCompleto(), r3);

        for (AppUser u : voters) {
            castVote(u, rep, pRep1);
        }

        log.info("Demo seed listo: 30 usuarios (contraseña {}), votaciones hasta {} (fin de domingo, America/Bogota). Directorio: 11/11/8 votos. Rep. IA: 30 votos a {}.",
                DEMO_PASSWORD, fechaFin, r1.getNombreCompleto());
    }

    private static Instant nextSundayEndOfDayBogota() {
        LocalDate today = LocalDate.now(BOGOTA);
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        ZonedDateTime end = sunday.atTime(23, 59, 59).atZone(BOGOTA);
        if (!end.isAfter(ZonedDateTime.now(BOGOTA))) {
            end = sunday.plusWeeks(1).atTime(23, 59, 59).atZone(BOGOTA);
        }
        return end.toInstant();
    }

    private Plancha savePlancha(ElectoralProcess proceso, Faculty facultad, CollegialBody body, String nombreLista, AppUser principal) {
        Plancha p = new Plancha();
        p.setNombre(nombreLista);
        p.setDescripcion(null);
        p.setEstado(EstadoPlancha.APROBADA);
        p.setProceso(proceso);
        p.setFacultad(facultad);
        p.setCollegialBody(body);
        p = planchaRepository.save(p);
        Candidato c = new Candidato();
        c.setPlancha(p);
        c.setUsuario(principal);
        c.setRol(RolCandidato.PRINCIPAL);
        candidatoRepository.save(c);
        return p;
    }

    private void castVote(AppUser user, ElectoralProcess proceso, Plancha plancha) {
        if (votoRepository.findByUsuarioIdAndProcesoId(user.getId(), proceso.getId()).isPresent()) {
            return;
        }
        Voto v = new Voto();
        v.setUsuario(user);
        v.setProceso(proceso);
        v.setPlancha(plancha);
        votoRepository.save(v);
    }
}
