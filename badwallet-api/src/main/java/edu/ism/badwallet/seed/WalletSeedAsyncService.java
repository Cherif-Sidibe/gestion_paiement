package edu.ism.badwallet.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Lance le seed en tache de fond (@Async) : l'endpoint repond 202 immediatement,
 * la generation se poursuit hors du thread de la requete. Delegue au moteur
 * WalletSeeder (bean distinct => le proxy @Transactional reste actif).
 */
@Service
public class WalletSeedAsyncService {

    private static final Logger log = LoggerFactory.getLogger(WalletSeedAsyncService.class);

    private final WalletSeeder walletSeeder;

    public WalletSeedAsyncService(WalletSeeder walletSeeder) {
        this.walletSeeder = walletSeeder;
    }

    @Async
    public void seedAsync(int numWallets, int eventsPerWallet) {
        log.info("Seed asynchrone demarre : {} wallets x {} evenements.", numWallets, eventsPerWallet);
        SeedResult result = walletSeeder.seed(numWallets, eventsPerWallet);
        log.info("Seed asynchrone termine : {} wallets et {} transactions.",
                result.wallets(), result.transactions());
    }
}
