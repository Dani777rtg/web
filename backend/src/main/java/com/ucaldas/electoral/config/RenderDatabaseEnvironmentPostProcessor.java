package com.ucaldas.electoral.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Render (y otros PaaS) inyectan {@code DATABASE_URL} como {@code postgres://user:pass@host/db}.
 * Convierte a propiedades estándar de Spring Boot para JDBC.
 * <p>
 * Si {@code DATABASE_URL} está definida, <strong>siempre</strong> tiene prioridad sobre
 * {@code spring.datasource.*} del {@code application.yml} local (evita quedar en localhost:5432 en prod).
 */
public class RenderDatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String SOURCE = "renderDatabaseUrl";
    private static final Logger LOG = Logger.getLogger(RenderDatabaseEnvironmentPostProcessor.class.getName());

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }
        // JDBC explícito por variable de entorno (sin parsear DATABASE_URL)
        String springDsUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        if (springDsUrl != null && !springDsUrl.isBlank()) {
            return;
        }
        String normalized = databaseUrl.trim();
        if (!normalized.startsWith("postgres")) {
            return;
        }
        try {
            if (normalized.startsWith("postgres://")) {
                normalized = "postgresql://" + normalized.substring("postgres://".length());
            }
            var uri = java.net.URI.create(normalized);
            String userInfo = uri.getRawUserInfo();
            if (userInfo == null) {
                return;
            }
            int colon = userInfo.indexOf(':');
            String user = URLDecoder.decode(
                    colon > 0 ? userInfo.substring(0, colon) : userInfo, StandardCharsets.UTF_8);
            String password = colon > 0
                    ? URLDecoder.decode(userInfo.substring(colon + 1), StandardCharsets.UTF_8)
                    : "";
            String host = uri.getHost();
            if (host == null) {
                return;
            }
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();
            if (path == null || path.length() < 2) {
                return;
            }
            String db = path.substring(1).split("\\?")[0];
            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db + "?sslmode=require";
            Map<String, Object> map = new HashMap<>();
            map.put("spring.datasource.url", jdbcUrl);
            map.put("spring.datasource.username", user);
            map.put("spring.datasource.password", password);
            environment.getPropertySources().addFirst(new MapPropertySource(SOURCE, map));
            LOG.log(Level.INFO, "DATABASE_URL aplicada a spring.datasource (host={0}, db={1})", new Object[]{host, db});
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo parsear DATABASE_URL; revise el formato (postgres://...)", e);
        }
    }

    private static void warnIfProdWithoutDatabaseUrl(ConfigurableEnvironment environment) {
        String active = environment.getProperty("SPRING_PROFILES_ACTIVE");
        if (active == null || !active.toLowerCase().contains("prod")) {
            return;
        }
        String jdbc = environment.getProperty("SPRING_DATASOURCE_URL");
        if (jdbc != null && !jdbc.isBlank()) {
            return;
        }
        LOG.warning("""
                Perfil prod sin DATABASE_URL ni SPRING_DATASOURCE_URL. \
                En Render: Environment → vincular PostgreSQL al servicio (o pegar DATABASE_URL). \
                Sin eso la API no puede arrancar.""");
    }
}
