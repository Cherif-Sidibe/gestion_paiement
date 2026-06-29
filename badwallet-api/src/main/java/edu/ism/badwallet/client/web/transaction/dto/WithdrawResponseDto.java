package edu.ism.badwallet.client.web.transaction.dto;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * Reponse d'un retrait : montant retire, frais appliques, type et etat a jour de la wallet.
 */
@Builder
public record WithdrawResponseDto(
        Long walletId,
        String code,
        String phoneNumber,
        String currency,
        BigDecimal amount,
        BigDecimal fees,
        BigDecimal newBalance,
        String type,
        String paymentMethod,
        String createdAt
) {
}
