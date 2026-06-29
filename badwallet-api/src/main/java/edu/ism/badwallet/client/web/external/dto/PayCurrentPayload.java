package edu.ism.badwallet.client.web.external.dto;

import java.math.BigDecimal;

/**
 * Corps envoye a payment-service POST /api/factures/pay-current.
 * service : libelle ISM|WOYAFAL, deserialise par payment-service dans son enum.
 */
public record PayCurrentPayload(String walletCode, String service, BigDecimal amount) {
}
