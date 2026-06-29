package edu.ism.badwallet.client.web.wallet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record WalletCreateRequestDto(
        @NotBlank(message = "Le numéro de téléphone du wallet est obligatoire")
        String phoneNumber,
        @NotBlank(message = "L'email du wallet est obligatoire")
        @Email(message = "L'email du wallet doit être valide")
        String email,
        @NotNull(message = "Le solde initial du wallet est obligatoire")
        @PositiveOrZero(message = "Le solde initial du wallet doit être positif ou nul")
        BigDecimal initialBalance,
        @NotBlank(message = "Le code du wallet est obligatoire")
        String code,
        @NotBlank(message = "La devise du wallet est obligatoire")
        String currency
) {
}
