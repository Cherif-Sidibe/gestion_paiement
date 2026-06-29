package edu.ism.badwallet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Active le support @Async : l'endpoint /seed repond 202 immediatement pendant
 * que la generation tourne dans un thread separe.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
