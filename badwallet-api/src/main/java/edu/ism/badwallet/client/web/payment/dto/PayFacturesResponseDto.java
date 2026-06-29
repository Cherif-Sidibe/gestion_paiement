package edu.ism.badwallet.client.web.payment.dto;

import java.math.BigDecimal;
import java.util.List;

import edu.ism.badwallet.client.web.external.dto.FactureDto;
import lombok.Builder;

/**
 * Reponse d'un paiement par references : montant total debite, nouveau solde de
 * la wallet et factures marquees payees cote payment-service.
 */
@Builder
public record PayFacturesResponseDto(
        String walletCode,
        String phoneNumber,
        BigDecimal montantDebite,
        BigDecimal newBalance,
        List<FactureDto> facturesPayees
) {
}
