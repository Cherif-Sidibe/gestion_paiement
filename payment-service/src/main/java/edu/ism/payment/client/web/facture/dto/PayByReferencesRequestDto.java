package edu.ism.payment.client.web.facture.dto;

import java.util.List;

import edu.ism.payment.facture.ServiceFacture;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// Corps de POST /api/factures/pay-by-references
public record PayByReferencesRequestDto(
        @NotBlank(message = "Le code wallet est obligatoire") String walletCode,
        @NotNull(message = "Le service est obligatoire") ServiceFacture service,
        @NotEmpty(message = "Au moins une reference de facture est obligatoire") List<String> references) {

}
