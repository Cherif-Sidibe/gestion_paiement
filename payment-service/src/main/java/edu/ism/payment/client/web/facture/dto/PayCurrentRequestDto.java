package edu.ism.payment.client.web.facture.dto;

import java.math.BigDecimal;

import edu.ism.payment.facture.ServiceFacture;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Corps de POST /api/factures/pay-current
public record PayCurrentRequestDto(
        @NotBlank(message = "Le code wallet est obligatoire") String walletCode,
        @NotNull(message = "Le service est obligatoire") ServiceFacture service,
        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit etre positif") BigDecimal amount) {

}
