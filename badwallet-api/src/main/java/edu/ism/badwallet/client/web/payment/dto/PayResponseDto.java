package edu.ism.badwallet.client.web.payment.dto;

import java.math.BigDecimal;

import edu.ism.badwallet.client.web.external.dto.FactureDto;
import lombok.Builder;

/**
 * Reponse d'un paiement du mois courant : montant debite, nouveau solde de la
 * wallet et facture marquee payee cote payment-service.
 */
@Builder
public record PayResponseDto(
        String walletCode,
        String phoneNumber,
        BigDecimal montantDebite,
        BigDecimal newBalance,
        FactureDto facturePayee
) {
}
