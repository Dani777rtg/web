package com.ucaldas.electoral.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Ejecuta {@link DemoDataSeedService} al arranque si {@code app.demo-seed.enabled=true}.
 */
@Component
@Order(100)
@ConditionalOnProperty(name = "app.demo-seed.enabled", havingValue = "true")
public class DemoDataSeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeedRunner.class);

    private final DemoDataSeedService demoDataSeedService;

    public DemoDataSeedRunner(DemoDataSeedService demoDataSeedService) {
        this.demoDataSeedService = demoDataSeedService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            demoDataSeedService.seedIfNeeded();
        } catch (Exception e) {
            log.error("Demo seed falló (puede ignorarse si no usa datos de prueba).", e);
        }
    }
}
