package edu.ism.badwallet.external.facture;

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
}
