package edu.ism.payment.facture;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ism.payment.shared.exceptions.FactureAlreadyPaidException;
import edu.ism.payment.shared.exceptions.FactureNotForWalletException;
import edu.ism.payment.shared.exceptions.FactureNotFoundException;
import edu.ism.payment.shared.exceptions.InvalidAmountException;

@Service
public class FactureServiceImpl implements FactureService {

    private static final Logger log = LoggerFactory.getLogger(FactureServiceImpl.class);

    private final FactureRepository factureRepository;

    public FactureServiceImpl(FactureRepository factureRepository) {
        this.factureRepository = factureRepository;
    }

    @Override
    public List<Facture> findCurrentUnpaid(String walletCode, ServiceFacture service) {
        LocalDate moisCourant = LocalDate.now().withDayOfMonth(1);
        if (service == null) {
            return factureRepository.findByWalletCodeAndMoisAndPayeeFalse(walletCode, moisCourant);
        }
        return factureRepository.findByWalletCodeAndServiceAndMoisAndPayeeFalse(walletCode, service, moisCourant);
    }

    @Override
    public List<Facture> findUnpaidInPeriod(String walletCode, LocalDate debut, LocalDate fin) {
        return factureRepository.findByWalletCodeAndPayeeFalseAndMoisBetween(walletCode, debut, fin);
    }

    @Override
    @Transactional
    public Facture payCurrent(String walletCode, ServiceFacture service, BigDecimal amount) {
        LocalDate moisCourant = LocalDate.now().withDayOfMonth(1);

        Facture facture = factureRepository.findByWalletCodeAndServiceAndMois(walletCode, service, moisCourant)
                .orElseThrow(() -> new FactureNotFoundException(
                        "Aucune facture " + service + " pour le mois courant sur la wallet " + walletCode));

        if (facture.isPayee()) {
            throw new FactureAlreadyPaidException(
                    "La facture " + facture.getReference() + " du mois courant est deja payee");
        }

        // Regle metier : le montant envoye doit correspondre au montant de la facture.
        if (facture.getMontant().compareTo(amount) != 0) {
            throw new InvalidAmountException(
                    "Le montant envoye (" + amount + ") ne correspond pas au montant de la facture "
                            + facture.getReference() + " (" + facture.getMontant() + ")");
        }

        facture.setPayee(true);
        facture.setDatePaiement(LocalDate.now());
        Facture saved = factureRepository.save(facture);
        log.info("Paiement mois courant : facture {} (wallet {}, service {}, montant recu {}).",
                saved.getReference(), walletCode, service, amount);
        return saved;
    }

    @Override
    @Transactional
    public List<Facture> payByReferences(String walletCode, ServiceFacture service, List<String> references) {
        // 1) Validation complete : on ne paie rien tant qu'une reference pose probleme.
        List<Facture> aPayer = new ArrayList<>();
        for (String reference : references) {
            Facture facture = factureRepository.findByReference(reference)
                    .orElseThrow(() -> new FactureNotFoundException("Facture introuvable : " + reference));

            if (!facture.getWalletCode().equals(walletCode)) {
                throw new FactureNotForWalletException(
                        "La facture " + reference + " n'appartient pas a la wallet " + walletCode);
            }
            if (facture.getService() != service) {
                throw new FactureNotForWalletException(
                        "La facture " + reference + " ne correspond pas au service " + service);
            }
            if (facture.isPayee()) {
                throw new FactureAlreadyPaidException("La facture " + reference + " est deja payee");
            }
            aPayer.add(facture);
        }

        // 2) Paiement : toutes les factures validees sont marquees payees.
        LocalDate aujourdhui = LocalDate.now();
        for (Facture facture : aPayer) {
            facture.setPayee(true);
            facture.setDatePaiement(aujourdhui);
        }
        List<Facture> saved = factureRepository.saveAll(aPayer);
        log.info("Paiement par references : {} facture(s) payee(s) sur la wallet {} (service {}).",
                saved.size(), walletCode, service);
        return saved;
    }
}
