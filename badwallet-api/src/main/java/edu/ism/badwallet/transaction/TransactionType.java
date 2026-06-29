package edu.ism.badwallet.transaction;

/**
 * Type metier d'une transaction (libelles en francais).
 * Mappe en base via @Enumerated(STRING) sur la colonne "type".
 */
public enum TransactionType {
    DEPOT,
    RETRAIT,
    TRANSFERT_ENVOYE,
    TRANSFERT_RECU,
    PAIEMENT
}
