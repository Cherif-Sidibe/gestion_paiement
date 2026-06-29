package edu.ism.badwallet.client.web.payment.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

// Corps de POST /api/wallets/pay-factures : paie les factures listees par reference.
public record PayFacturesRequestDto(
        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        String phoneNumber,
        @NotBlank(message = "Le service est obligatoire")
        String serviceName,
        @NotEmpty(message = "Au moins une référence de facture est obligatoire")
        List<String> factureReferences
) {
}
