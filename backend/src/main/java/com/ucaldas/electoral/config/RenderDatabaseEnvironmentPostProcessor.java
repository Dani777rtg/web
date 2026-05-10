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
        if (explicitSpringDatasourceUrl(environment)) {
            return;
        }

        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            failIfProdWithoutDatabaseConfig(environment);
            return;
        }

        String normalized = databaseUrl.trim();
        if (!normalized.startsWith("postgres")) {
            throw new IllegalStateException(
                    "DATABASE_URL no reconocida (debe empezar por postgres:// o postgresql://). Valor recibido (recortado): "
                            + preview(normalized));
        }
        try {
            if (normalized.startsWith("postgres://")) {
                normalized = "postgresql://" + normalized.substring("postgres://".length());
            }
            var uri = java.net.URI.create(normalized);
            String userInfo = uri.getRawUserInfo();
            if (userInfo == null || userInfo.isBlank()) {
                throw new IllegalStateException("DATABASE_URL sin usuario/contraseña en la URL.");
            }
            int colon = userInfo.indexOf(':');
            String user = URLDecoder.decode(
                    colon > 0 ? userInfo.substring(0, colon) : userInfo, StandardCharsets.UTF_8);
            String password = colon > 0
                    ? URLDecoder.decode(userInfo.substring(colon + 1), StandardCharsets.UTF_8)
                    : "";
            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                throw new IllegalStateException("DATABASE_URL sin host.");
            }
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();
            if (path == null || path.length() < 2) {
                throw new IllegalStateException("DATABASE_URL sin nombre de base en el path.");
            }
            String db = path.substring(1).split("\\?")[0];
            if (db.isBlank()) {
                throw new IllegalStateException("DATABASE_URL con nombre de base vacío.");
            }
            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db + "?sslmode=require";
            Map<String, Object> map = new HashMap<>();
            map.put("spring.datasource.url", jdbcUrl);
            map.put("spring.datasource.username", user);
            map.put("spring.datasource.password", password);
            environment.getPropertySources().addFirst(new MapPropertySource(SOURCE, map));
            LOG.log(Level.INFO, "DATABASE_URL aplicada a spring.datasource (host={0}, db={1})", new Object[]{host, db});
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo convertir DATABASE_URL a JDBC. Revise la URL en Render (Internal). Detalle: " + e.getMessage(),
                    e);
        }
    }

    private static boolean explicitSpringDatasourceUrl(ConfigurableEnvironment environment) {
        String springDsUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        return springDsUrl != null && !springDsUrl.isBlank();
    }

    private static void failIfProdWithoutDatabaseConfig(ConfigurableEnvironment environment) {
        if (!isProdProfile(environment)) {
            return;
        }
        if (explicitSpringDatasourceUrl(environment)) {
            return;
        }
        throw new IllegalStateException("""
                Perfil prod sin conexión a Postgres: falta DATABASE_URL (vinculá PostgreSQL al Web Service en Render) \
                o defina SPRING_DATASOURCE_URL con el jdbc:postgresql://...""");
    }

    private static boolean isProdProfile(ConfigurableEnvironment environment) {
        String active = environment.getProperty("SPRING_PROFILES_ACTIVE");
        return active != null && active.toLowerCase().contains("prod");
    }

    private static String preview(String s) {
        if (s.length() <= 80) {
            return s;
        }
        return s.substring(0, 80) + "...";
    }
}
