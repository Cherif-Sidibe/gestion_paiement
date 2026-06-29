package edu.ism.badwallet.client.web.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequestDto(
        @NotBlank(message = "Le numéro de téléphone de l'émetteur est obligatoire")
        String senderPhone,
        @NotBlank(message = "Le numéro de téléphone du destinataire est obligatoire")
        String receiverPhone,
        @NotNull(message = "Le montant du transfert est obligatoire")
        @Positive(message = "Le montant du transfert doit être strictement positif")
        BigDecimal amount
) {
}
