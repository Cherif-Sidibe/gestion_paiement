package edu.ism.badwallet.seed;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de peuplement. Repond 202 ACCEPTED immediatement ; le seed s'execute
 * en tache de fond via WalletSeedAsyncService.
 */
@RestController
@RequestMapping("/api/wallets")
public class WalletSeedController {

    private final WalletSeedAsyncService walletSeedAsyncService;

    public WalletSeedController(WalletSeedAsyncService walletSeedAsyncService) {
        this.walletSeedAsyncService = walletSeedAsyncService;
    }

    @PostMapping("/seed")
    public ResponseEntity<Void> seed(
            @RequestParam(defaultValue = "10") int numWallets,
            @RequestParam(defaultValue = "40") int eventsPerWallet) {
        walletSeedAsyncService.seedAsync(numWallets, eventsPerWallet);
        return ResponseEntity.accepted().build();
    }
}
