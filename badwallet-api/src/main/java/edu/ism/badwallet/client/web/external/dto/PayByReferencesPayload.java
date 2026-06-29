package edu.ism.badwallet.client.web.external.dto;

import java.util.List;

/**
 * Corps envoye a payment-service POST /api/factures/pay-by-references.
 * service : libelle ISM|WOYAFAL, deserialise par payment-service dans son enum.
 */
public record PayByReferencesPayload(String walletCode, String service, List<String> references) {
}
