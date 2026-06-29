package edu.ism.badwallet.client.web.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WithdrawRequestDto(
        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        String phoneNumber,
        @NotNull(message = "Le montant du retrait est obligatoire")
        @Positive(message = "Le montant du retrait doit être strictement positif")
        BigDecimal amount
) {
}
