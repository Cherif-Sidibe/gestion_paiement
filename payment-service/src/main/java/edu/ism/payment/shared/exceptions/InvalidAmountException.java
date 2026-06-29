package edu.ism.payment.shared.exceptions;

// 400 : le montant envoye ne correspond pas au montant de la facture du mois courant.
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
