package edu.ism.badwallet.transaction;

/**
 * Methode de paiement d'un depot.
 * Mappe en base via @Enumerated(STRING) sur la colonne "payment_method".
 */
public enum PaymentMethod {
    CREDIT_CARD,
    WALLET_TARGET
}
