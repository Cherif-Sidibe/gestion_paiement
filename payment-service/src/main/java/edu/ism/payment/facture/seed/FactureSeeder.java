package edu.ism.payment.facture.seed;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import edu.ism.payment.facture.Facture;
import edu.ism.payment.facture.FactureRepository;
import edu.ism.payment.facture.ServiceFacture;
import edu.ism.payment.facture.provider.FactureProvider;
import edu.ism.payment.facture.provider.FactureProviderFactory;

/**
 * Seed des factures au demarrage (CommandLineRunner, style projet de reference).
 *
 * Pour chaque wallet WLT-0000001 .. WLT-0000010, genere une facture mensuelle
 * pour CHAQUE service (ISM ET WOYAFAL), de janvier 2026 jusqu'au mois courant
 * (au moins jusqu'a juillet 2026), afin de couvrir la fenetre 2026-05 -> 2026-07.
 *
 * Patterns exhibes ici :
 *  - FACTORY  : FactureProviderFactory aiguille vers la bonne strategie.
 *  - STRATEGY : chaque FactureProvider calcule montant/libelle de son service.
 *  - BUILDER  : construction des Facture via le builder Lombok.
 */
@Component
public class FactureSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(FactureSeeder.class);

    private static final int NB_WALLETS = 10;
    private static final LocalDate PREMIER_MOIS = LocalDate.of(2026, 1, 1);
    // Fenetre minimale a couvrir, meme si on demarre avant juillet 2026.
    private static final LocalDate DERNIER_MOIS_MIN = LocalDate.of(2026, 7, 1);

    private final FactureRepository factureRepository;
    private final FactureProviderFactory providerFactory;

    public FactureSeeder(FactureRepository factureRepository, FactureProviderFactory providerFactory) {
        this.factureRepository = factureRepository;
        this.providerFactory = providerFactory;
    }

    @Override
    public void run(String... args) throws Exception {
        if (factureRepository.count() > 0) {
            return;
        }

        LocalDate moisCourant = LocalDate.now().withDayOfMonth(1);
        // On va jusqu'au mois courant, mais au minimum jusqu'a juillet 2026.
        LocalDate dernierMois = moisCourant.isAfter(DERNIER_MOIS_MIN) ? moisCourant : DERNIER_MOIS_MIN;
        // Mois courant ET mois precedent toujours impayes (factures recentes).
        LocalDate moisPrecedent = moisCourant.minusMonths(1);

        List<Facture> aGenerer = new ArrayList<>();

        for (int numeroWallet = 1; numeroWallet <= NB_WALLETS; numeroWallet++) {
            String walletCode = String.format("WLT-%07d", numeroWallet);

            for (ServiceFacture service : ServiceFacture.values()) {
                // FACTORY -> STRATEGY : on recupere le provider du service.
                FactureProvider provider = providerFactory.getProvider(service);

                int sequence = 0;
                for (LocalDate mois = PREMIER_MOIS; !mois.isAfter(dernierMois); mois = mois.plusMonths(1)) {
                    sequence++;

                    // Reference : FAC-<SERVICE>-<numeroWallet>-<sequence> (1 = la plus ancienne).
                    String reference = "FAC-" + service.name() + "-" + numeroWallet + "-" + sequence;

                    // Statut : mois courant + precedent (et futurs) impayes ;
                    // mois plus anciens payes si sequence paire, impayes si impaire
                    // -> garantit un mix d'impayees, dont FAC-ISM-3-1 et FAC-ISM-3-3.
                    boolean moisRecent = !mois.isBefore(moisPrecedent);
                    boolean payee = !moisRecent && (sequence % 2 == 0);
                    LocalDate datePaiement = payee ? mois.plusDays(9) : null;

                    // BUILDER : construction de la Facture via le builder Lombok.
                    Facture facture = Facture.builder()
                            .reference(reference)
                            .walletCode(walletCode)
                            .service(service)
                            .montant(provider.montantPour(mois))
                            .mois(mois)
                            .payee(payee)
                            .datePaiement(datePaiement)
                            .build();

                    aGenerer.add(facture);
                }
            }
        }

        factureRepository.saveAll(aGenerer);
        log.info("Seed factures : {} factures generees ({} wallets x {} services, {} -> {}).",
                aGenerer.size(), NB_WALLETS, ServiceFacture.values().length, PREMIER_MOIS, dernierMois);
    }
}
