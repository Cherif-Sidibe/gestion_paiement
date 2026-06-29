package edu.ism.payment.shared.exceptions;

// 404 : facture (par reference ou pour le mois courant) introuvable.
public class FactureNotFoundException extends RuntimeException {
    public FactureNotFoundException(String message) {
        super(message);
    }
}
