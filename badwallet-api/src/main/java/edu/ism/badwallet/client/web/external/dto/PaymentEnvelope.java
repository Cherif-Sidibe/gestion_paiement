package edu.ism.badwallet.client.web.external.dto;

/**
 * Enveloppe RestResponse<T> renvoyee par payment-service.
 * Seul le champ body nous interesse ; les autres champs sont ignores.
 */
public record PaymentEnvelope<T>(T body) {
}
