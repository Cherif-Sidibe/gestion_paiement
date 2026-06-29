package edu.ism.badwallet.client.web.transaction.dto;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * Ligne d'historique : une transaction de la wallet (type, montant, frais, methode, date).
 * paymentMethod renseigne uniquement pour les depots, null sinon.
 */
@Builder
public record TransactionResponseDto(
        Long id,
        String type,
        BigDecimal amount,
        BigDecimal fees,
        String paymentMethod,
        String createdAt
) {
}
