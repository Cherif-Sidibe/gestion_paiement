package edu.ism.badwallet.seed;

import edu.ism.badwallet.wallet.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Auto-seed au demarrage (CommandLineRunner).
 *
 * Si la base est vide, genere un jeu par defaut pour que le front ait toujours
 * des donnees fraiches (la base H2 en memoire se vide a chaque redemarrage). La
 * garde "count == 0" evite tout doublon si des wallets existent deja.
 */
@Component
public class WalletSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(WalletSeedRunner.class);

    // Jeu par defaut : 10 wallets, ~40 transactions chacun.
    private static final int DEFAULT_WALLETS = 10;
    private static final int DEFAULT_EVENTS = 40;

    private final WalletRepository walletRepository;
    private final WalletSeeder walletSeeder;

    public WalletSeedRunner(WalletRepository walletRepository, WalletSeeder walletSeeder) {
        this.walletRepository = walletRepository;
        this.walletSeeder = walletSeeder;
    }

    @Override
    public void run(String... args) {
        if (walletRepository.count() > 0) {
            return;
        }
        SeedResult result = walletSeeder.seed(DEFAULT_WALLETS, DEFAULT_EVENTS);
        log.info("Auto-seed badwallet au demarrage : {} wallets et {} transactions.",
                result.wallets(), result.transactions());
    }
}
