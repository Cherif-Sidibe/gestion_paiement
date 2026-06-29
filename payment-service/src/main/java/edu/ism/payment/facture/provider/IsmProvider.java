package edu.ism.payment.facture.provider;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import edu.ism.payment.facture.ServiceFacture;

/**
 * PATTERN STRATEGY (implementation ISM).
 * Frais de scolarite mensuels : montant fixe en XOF.
 */
@Component
public class IsmProvider implements FactureProvider {

    @Override
    public ServiceFacture getService() {
        return ServiceFacture.ISM;
    }

    @Override
    public BigDecimal montantPour(LocalDate mois) {
        return BigDecimal.valueOf(150000);
    }

    @Override
    public String libelle() {
        return "Scolarite ISM";
    }
}
