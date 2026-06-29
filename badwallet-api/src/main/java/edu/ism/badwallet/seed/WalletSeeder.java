package edu.ism.badwallet.seed;

import edu.ism.badwallet.transaction.PaymentMethod;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.TransactionRepository;
import edu.ism.badwallet.transaction.TransactionType;
import edu.ism.badwallet.wallet.Wallet;
import edu.ism.badwallet.wallet.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Moteur de peuplement (synchrone, transactionnel).
 *
 * Genere des wallets coherents avec payment-service (codes WLT-0000001.., memes
 * telephones) puis un historique de transactions quasi-reel par wallet. Le solde
 * est recalcule au fil de l'eau : un debit n'est jamais emis s'il rendrait le
 * solde negatif (sinon repli en depot). Persistance en batch via saveAll.
 *
 * Patterns : Builder (Wallet.builder(), Transaction.builder()).
 */
@Service
public class WalletSeeder {

    private static final Logger log = LoggerFactory.getLogger(WalletSeeder.class);

    // Memes regles que WithdrawOperation : frais = min(montant * 1%, plafond), entier XOF.
    private static final BigDecimal FEE_RATE = new BigDecimal("0.01");
    private static final BigDecimal FEE_CAP = new BigDecimal("5000");

    // Historique reparti sur les 6 dernieres semaines.
    private static final int WINDOW_DAYS = 42;

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletSeeder(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public SeedResult seed(int numWallets, int eventsPerWallet) {
        // Reprend la numerotation apres les wallets existants (codes sequentiels)
        // pour que /seed ajoute des wallets sans collision de code/telephone.
        int start = (int) walletRepository.count() + 1;
        Random rnd = new Random(31L * start + numWallets);

        LocalDateTime windowStart = LocalDateTime.now().minusDays(WINDOW_DAYS);
        // Pas moyen entre deux evenements pour etaler les transactions sur la fenetre.
        long totalEvents = Math.max(1L, (long) numWallets * eventsPerWallet);
        long avgStepSeconds = Math.max(1L, (WINDOW_DAYS * 24L * 3600L) / totalEvents);

        // 1) Wallets construits via le Builder Lombok, persistes en batch.
        List<Wallet> wallets = new ArrayList<>(numWallets);
        for (int i = 0; i < numWallets; i++) {
            int n = start + i;
            long initial = roundXof(100_000L + rnd.nextInt(900_001)); // 100k..1M XOF
            Wallet wallet = Wallet.builder()                          // Builder
                    .code(String.format("WLT-%07d", n))
                    .phoneNumber(String.format("+22177%07d", n))
                    .email("client" + n + "@badwallet.sn")
                    .currency("XOF")
                    .balance(BigDecimal.valueOf(initial))
                    .createdAt(windowStart)
                    .build();
            wallets.add(wallet);
        }
        walletRepository.saveAll(wallets);

        // 2) Transactions quasi-reelles. Curseur global monotone => l'ordre de
        // generation == l'ordre chronologique : un debit est toujours controle
        // sur le solde courant, donc l'historique relu reste >= 0.
        List<Transaction> allTx = new ArrayList<>();
        LocalDateTime cursor = windowStart;
        for (Wallet wallet : wallets) {
            for (int e = 0; e < eventsPerWallet; e++) {
                cursor = cursor.plusSeconds(1 + (long) (rnd.nextDouble() * 2 * avgStepSeconds));
                allTx.addAll(buildEvent(wallet, wallets, rnd, cursor));
            }
        }

        // 3) Soldes finaux + transactions persistes en batch.
        walletRepository.saveAll(wallets);
        transactionRepository.saveAll(allTx);

        log.info("Seed badwallet : {} wallets (WLT-{} -> WLT-{}) et {} transactions generees.",
                numWallets, String.format("%07d", start), String.format("%07d", start + numWallets - 1),
                allTx.size());
        return new SeedResult(numWallets, allTx.size());
    }

    // Choisit un type d'evenement ; aucun debit ne peut rendre le solde negatif.
    private List<Transaction> buildEvent(Wallet wallet, List<Wallet> pool, Random rnd, LocalDateTime at) {
        int dice = rnd.nextInt(100);

        // RETRAIT (35%) si le solde couvre montant + frais, sinon repli en depot.
        if (dice < 35) {
            BigDecimal amount = BigDecimal.valueOf(roundXof(2_000L + rnd.nextInt(98_001)));
            BigDecimal fees = computeFees(amount);
            if (wallet.getBalance().compareTo(amount.add(fees)) >= 0) {
                wallet.setBalance(wallet.getBalance().subtract(amount.add(fees)));
                return List.of(Transaction.builder()                  // Builder
                        .wallet(wallet)
                        .type(TransactionType.RETRAIT)
                        .amount(amount)
                        .fees(fees)
                        .createdAt(at)
                        .build());
            }
            return List.of(deposit(wallet, rnd, at));
        }

        // TRANSFERT (20%) entre wallets seedees si le solde couvre le montant.
        if (dice < 55 && pool.size() > 1) {
            Wallet receiver = pickOther(wallet, pool, rnd);
            BigDecimal amount = BigDecimal.valueOf(roundXof(2_000L + rnd.nextInt(78_001)));
            if (wallet.getBalance().compareTo(amount) >= 0) {
                wallet.setBalance(wallet.getBalance().subtract(amount));
                receiver.setBalance(receiver.getBalance().add(amount));
                Transaction sent = Transaction.builder()              // Builder
                        .wallet(wallet)
                        .type(TransactionType.TRANSFERT_ENVOYE)
                        .amount(amount)
                        .fees(BigDecimal.ZERO)
                        .createdAt(at)
                        .build();
                Transaction received = Transaction.builder()          // Builder
                        .wallet(receiver)
                        .type(TransactionType.TRANSFERT_RECU)
                        .amount(amount)
                        .fees(BigDecimal.ZERO)
                        .createdAt(at)
                        .build();
                return List.of(sent, received);
            }
            return List.of(deposit(wallet, rnd, at));
        }

        // DEPOT (le reste, ou repli) : toujours possible, credite le solde.
        return List.of(deposit(wallet, rnd, at));
    }

    private Transaction deposit(Wallet wallet, Random rnd, LocalDateTime at) {
        BigDecimal amount = BigDecimal.valueOf(roundXof(5_000L + rnd.nextInt(195_001)));
        PaymentMethod method = rnd.nextBoolean() ? PaymentMethod.CREDIT_CARD : PaymentMethod.WALLET_TARGET;
        wallet.setBalance(wallet.getBalance().add(amount));
        return Transaction.builder()                                  // Builder
                .wallet(wallet)
                .type(TransactionType.DEPOT)
                .amount(amount)
                .fees(BigDecimal.ZERO)
                .paymentMethod(method)
                .createdAt(at)
                .build();
    }

    private static Wallet pickOther(Wallet self, List<Wallet> pool, Random rnd) {
        Wallet other;
        do {
            other = pool.get(rnd.nextInt(pool.size()));
        } while (other == self);
        return other;
    }

    private static BigDecimal computeFees(BigDecimal amount) {
        return amount.multiply(FEE_RATE).min(FEE_CAP).setScale(0, RoundingMode.HALF_UP);
    }

    // Montants XOF entiers, arrondis au millier pour rester realistes.
    private static long roundXof(long amount) {
        return (amount / 1_000L) * 1_000L;
    }
}
