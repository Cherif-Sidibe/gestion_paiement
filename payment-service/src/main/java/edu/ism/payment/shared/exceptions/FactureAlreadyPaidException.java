package edu.ism.payment.shared.exceptions;

// 409 : tentative de paiement d'une facture deja payee.
public class FactureAlreadyPaidException extends RuntimeException {
    public FactureAlreadyPaidException(String message) {
        super(message);
    }
}
