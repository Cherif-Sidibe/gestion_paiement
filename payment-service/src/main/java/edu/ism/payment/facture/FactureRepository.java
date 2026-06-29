package edu.ism.payment.facture;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    // Toutes les factures (impayees comprises) d'une wallet.
    List<Facture> findByWalletCode(String walletCode);

    // Factures impayees d'une wallet (tous services).
    List<Facture> findByWalletCodeAndPayeeFalse(String walletCode);

    // Factures impayees du mois donne (tous services) -> endpoint /current.
    List<Facture> findByWalletCodeAndMoisAndPayeeFalse(String walletCode, LocalDate mois);

    // Factures impayees du mois donne filtrees par service -> endpoint /current?unite=...
    List<Facture> findByWalletCodeAndServiceAndMoisAndPayeeFalse(String walletCode, ServiceFacture service, LocalDate mois);

    // Factures impayees dont le mois est dans l'intervalle -> endpoint /periode.
    List<Facture> findByWalletCodeAndPayeeFalseAndMoisBetween(String walletCode, LocalDate debut, LocalDate fin);

    // La facture (unique) d'une wallet pour un service et un mois -> pay-current.
    Optional<Facture> findByWalletCodeAndServiceAndMois(String walletCode, ServiceFacture service, LocalDate mois);

    // Recherche par reference -> pay-by-references.
    Optional<Facture> findByReference(String reference);

    boolean existsByReference(String reference);
}
