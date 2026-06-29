package edu.ism.payment.facture.provider;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import edu.ism.payment.facture.ServiceFacture;

/**
 * PATTERN FACTORY.
 * Retourne la bonne strategie (FactureProvider) selon l'enum ServiceFacture.
 * Spring injecte toutes les implementations de FactureProvider ; on les indexe
 * par service. Utilise par le seed, et reutilisable au moment du paiement.
 */
@Component
public class FactureProviderFactory {

    private final Map<ServiceFacture, FactureProvider> providers = new EnumMap<>(ServiceFacture.class);

    public FactureProviderFactory(List<FactureProvider> providerList) {
        for (FactureProvider provider : providerList) {
            providers.put(provider.getService(), provider);
        }
    }

    public FactureProvider getProvider(ServiceFacture service) {
        FactureProvider provider = providers.get(service);
        if (provider == null) {
            throw new IllegalArgumentException("Aucun provider pour le service " + service);
        }
        return provider;
    }
}
