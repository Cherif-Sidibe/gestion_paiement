package edu.ism.badwallet.shared.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Le service de factures (payment-service) a refuse le paiement
 * (facture inconnue, deja payee, montant errone, service invalide).
 * Porte le statut a renvoyer au client pour traduire proprement le refus
 * distant en erreur metier (404/409/400) plutot qu'en 500 brut.
 */
public class PaymentRejectedException extends RuntimeException {

    private final HttpStatus status;

    public PaymentRejectedException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
