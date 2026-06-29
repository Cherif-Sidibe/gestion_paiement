package edu.ism.payment.facture.provider;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import edu.ism.payment.facture.ServiceFacture;

/**
 * PATTERN STRATEGY (implementation WOYAFAL).
 * Facture d'electricite : montant XOF qui varie legerement selon le mois
 * (15000 / 20000 / 25000), reste sans decimale.
 */
@Component
public class WoyafalProvider implements FactureProvider {

    @Override
    public ServiceFacture getService() {
        return ServiceFacture.WOYAFAL;
    }

    @Override
    public BigDecimal montantPour(LocalDate mois) {
        long montant = 15000 + (mois.getMonthValue() % 3) * 5000L;
        return BigDecimal.valueOf(montant);
    }

    @Override
    public String libelle() {
        return "Electricite WOYAFAL";
    }
}
