package edu.ism.payment.client.web.facture.dto;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record FactureResponseDto(
        Long id,
        String reference,
        String walletCode,
        String service,
        BigDecimal montant,
        String mois,
        boolean payee,
        String datePaiement) {

}
