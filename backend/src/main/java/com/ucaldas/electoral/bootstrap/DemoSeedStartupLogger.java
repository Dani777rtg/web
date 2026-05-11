package com.ucaldas.electoral.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Deja en los logs si el seed de datos demo está activo (Render/prod suele tenerlo en false).
 */
@Component
public class DemoSeedStartupLogger implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(DemoSeedStartupLogger.class);

    private final Environment env;

    public DemoSeedStartupLogger(Environment env) {
        this.env = env;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        boolean enabled = Boolean.parseBoolean(env.getProperty("app.demo-seed.enabled", "false"));
        if (enabled) {
            log.info("Demo seed: habilitado. Si era la primera vez sin proceso demo, se insertaron usuarios y votaciones de prueba.");
        } else {
            log.info("Demo seed: deshabilitado (defecto en prod). Para cargar las 30 cuentas y votaciones de prueba en Render, agregue la variable de entorno DEMO_SEED_ENABLED=true y vuelva a desplegar. Detalle: docs/CUENTAS_DEMO_VOTACIONES.md");
        }
    }
}
