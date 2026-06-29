package edu.ism.badwallet.external.facture;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import edu.ism.badwallet.client.web.external.dto.FactureDto;

/**
 * Contrat local du service de factures externe.
 * L'implementation distante (proxy) relaie chaque appel vers payment-service.
 */
public interface ExternalFactureService {

    List<FactureDto> getCurrentUnpaid(String walletCode, String unite);

    List<FactureDto> getUnpaidInPeriod(String walletCode, LocalDate debut, LocalDate fin);

    // Marque payee la facture du mois courant ; leve une exception si payment-service refuse.
    FactureDto payCurrent(String walletCode, String service, BigDecimal amount);

    // Marque payees les factures listees (tout ou rien) ; leve une exception si refus.
    List<FactureDto> payByReferences(String walletCode, String service, List<String> references);
}
