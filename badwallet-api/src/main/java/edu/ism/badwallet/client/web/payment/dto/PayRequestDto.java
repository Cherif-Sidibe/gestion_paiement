package edu.ism.badwallet.client.web.payment.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Corps de POST /api/wallets/pay : paie la facture du mois courant pour ce service.
public record PayRequestDto(
        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        String phoneNumber,
        @NotBlank(message = "Le service est obligatoire")
        String serviceName,
        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit être strictement positif")
        BigDecimal amount
) {
}
