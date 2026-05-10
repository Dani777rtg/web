package com.ucaldas.electoral.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Render inyecta {@code DATABASE_URL} (postgres://…). Spring Boot no la convierte sola a JDBC.
 * Este bean corre en {@code prod} y evita depender del {@code EnvironmentPostProcessor} (a veces no se carga igual en el fat jar).
 */
@AutoConfiguration
@Profile("prod")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class RenderPostgresDataSourceConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(DataSource.class)
    DataSource prodDataSource(Environment env) {
        String databaseUrl = env.getProperty("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            return buildFromRenderDatabaseUrl(databaseUrl.trim());
        }
        String jdbcUrl = env.getProperty("spring.datasource.url");
        if (jdbcUrl != null && !jdbcUrl.isBlank()) {
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(jdbcUrl);
            ds.setUsername(env.getProperty("spring.datasource.username", ""));
            ds.setPassword(env.getProperty("spring.datasource.password", ""));
            return ds;
        }
        throw new IllegalStateException(
                "Perfil prod sin conexión a Postgres: falta DATABASE_URL (vinculá la BD al Web Service en Render) "
                        + "o defina spring.datasource.url (p. ej. SPRING_DATASOURCE_URL con jdbc:postgresql://…). "
                        + "Compruebe que las variables estén en el servicio Web de la API, no solo en la base de datos.");
    }

    private static DataSource buildFromRenderDatabaseUrl(String raw) {
        if (!raw.startsWith("postgres")) {
            throw new IllegalStateException("DATABASE_URL debe empezar por postgres:// o postgresql://");
        }
        try {
            String normalized = raw;
            if (normalized.startsWith("postgres://")) {
                normalized = "postgresql://" + normalized.substring("postgres://".length());
            }
            var uri = java.net.URI.create(normalized);
            String userInfo = uri.getRawUserInfo();
            if (userInfo == null || userInfo.isBlank()) {
                throw new IllegalStateException("DATABASE_URL sin usuario/contraseña.");
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
                throw new IllegalStateException("DATABASE_URL sin nombre de base.");
            }
            String db = path.substring(1).split("\\?")[0];
            if (db.isBlank()) {
                throw new IllegalStateException("DATABASE_URL con base vacía.");
            }
            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db + "?sslmode=require";
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(jdbcUrl);
            ds.setUsername(user);
            ds.setPassword(password);
            return ds;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo interpretar DATABASE_URL: " + e.getMessage(), e);
        }
    }
}
