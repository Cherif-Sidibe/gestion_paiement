package edu.ism.badwallet.client.web.wallet.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record WalletBalanceResponseDto(
        String code,
        String phoneNumber,
        BigDecimal balance,
        String currency
) {
}
