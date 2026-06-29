package edu.ism.badwallet.client.web.wallet.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record WalletCreateResponseDto(
        Long id,
        String code,
        String phoneNumber,
        String email,
        String currency,
        BigDecimal balance,
        String createdAt
) {
}
