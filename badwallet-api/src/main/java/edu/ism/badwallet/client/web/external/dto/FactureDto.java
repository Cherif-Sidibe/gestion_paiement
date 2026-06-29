package edu.ism.badwallet.client.web.external.dto;

import java.math.BigDecimal;

/**
 * Facture telle que renvoyee par payment-service.
 * Sert a deserialiser la reponse distante puis a la relayer au client.
 */
public record FactureDto(
        Long id,
        String reference,
        String walletCode,
        String service,
        BigDecimal montant,
        String mois,
        boolean payee,
        String datePaiement) {
}
