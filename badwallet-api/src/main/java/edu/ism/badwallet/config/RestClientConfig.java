package edu.ism.badwallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configure le client HTTP utilise par le proxy de factures.
 * L'URL de payment-service provient de application.properties (jamais codee en dur).
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient paymentRestClient(@Value("${payment-service.base-url}") String baseUrl) {
        return RestClient.create(baseUrl);
    }
}
