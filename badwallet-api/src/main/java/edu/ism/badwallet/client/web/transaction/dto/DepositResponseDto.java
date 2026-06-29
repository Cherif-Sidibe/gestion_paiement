package edu.ism.badwallet.client.web.transaction.dto;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * Reponse d'un depot : montant, type, methode et etat a jour de la wallet.
 */
@Builder
public record DepositResponseDto(
        Long walletId,
        String code,
        String phoneNumber,
        String currency,
        BigDecimal amount,
        BigDecimal newBalance,
        String type,
        String paymentMethod,
        String createdAt
) {
}
