package edu.ism.payment.facture;

/**
 * Prestataires de factures supportes par payment-service.
 * Persiste en base via @Enumerated(EnumType.STRING) sur l'entite Facture.
 */
public enum ServiceFacture {
    ISM, WOYAFAL
}
