package edu.ism.payment.facture;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FactureService {

    // Factures impayees du mois courant (service == null => tous services).
    List<Facture> findCurrentUnpaid(String walletCode, ServiceFacture service);

    // Factures impayees dont le mois est dans [debut, fin].
    List<Facture> findUnpaidInPeriod(String walletCode, LocalDate debut, LocalDate fin);

    // Paie la facture impayee du mois courant pour ce service.
    Facture payCurrent(String walletCode, ServiceFacture service, BigDecimal amount);

    // Paie les factures listees (validation complete d'abord, paiement ensuite).
    List<Facture> payByReferences(String walletCode, ServiceFacture service, List<String> references);
}
