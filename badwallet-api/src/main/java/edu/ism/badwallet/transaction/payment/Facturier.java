package edu.ism.badwallet.transaction.payment;

import org.springframework.http.HttpStatus;

import edu.ism.badwallet.shared.exceptions.PaymentRejectedException;

/**
 * Prestataires de factures relayes vers payment-service.
 * Le nom canonique (name()) est envoye tel quel dans le corps HTTP : payment-service
 * le deserialise dans sa propre enum ServiceFacture.
 */
public enum Facturier {
    ISM, WOYAFAL;

    // Factory : aiguille le libelle recu (ISM|WOYAFAL) vers le facturier, sinon erreur metier 400.
    public static Facturier from(String serviceName) {
        if (serviceName == null) {
            throw new PaymentRejectedException(HttpStatus.BAD_REQUEST,
                    "Service de facture obligatoire (attendu ISM ou WOYAFAL)");
        }
        try {
            return Facturier.valueOf(serviceName.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new PaymentRejectedException(HttpStatus.BAD_REQUEST,
                    "Service de facture inconnu : " + serviceName + " (attendu ISM ou WOYAFAL)");
        }
    }
}
