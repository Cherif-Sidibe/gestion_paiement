package edu.ism.payment.facture.provider;

import java.math.BigDecimal;
import java.time.LocalDate;

import edu.ism.payment.facture.ServiceFacture;

/**
 * PATTERN STRATEGY.
 * Interface commune a tous les prestataires de factures. Chaque implementation
 * (IsmProvider, WoyafalProvider) encapsule la logique propre a son service :
 * montant facture pour un mois donne et libelle. Le seed (et plus tard le
 * paiement) itere sur ces strategies sans connaitre leur detail.
 */
public interface FactureProvider {

    // Le service couvert par cette strategie (sert a la Factory pour l'aiguillage).
    ServiceFacture getService();

    // Montant XOF (entier, sans decimale) facture pour le mois donne.
    BigDecimal montantPour(LocalDate mois);

    // Libelle lisible du prestataire.
    String libelle();
}
