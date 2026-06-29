package edu.ism.badwallet.client.web.transaction.dto;

import edu.ism.badwallet.transaction.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositRequestDto(
        @NotNull(message = "Le montant du dépôt est obligatoire")
        @Positive(message = "Le montant du dépôt doit être strictement positif")
        BigDecimal amount,
        @NotNull(message = "La méthode de paiement est obligatoire")
        PaymentMethod paymentMethod
) {
}
