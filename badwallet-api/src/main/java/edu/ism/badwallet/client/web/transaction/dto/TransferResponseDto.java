package edu.ism.badwallet.client.web.transaction.dto;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * Reponse d'un transfert : parties, montant, frais et nouveau solde de l'emetteur.
 */
@Builder
public record TransferResponseDto(
        String senderPhone,
        String receiverPhone,
        String currency,
        BigDecimal amount,
        BigDecimal fees,
        BigDecimal newSenderBalance,
        String type,
        String createdAt
) {
}
