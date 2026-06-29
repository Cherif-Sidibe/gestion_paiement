package edu.ism.badwallet.transaction.withdraw;

import edu.ism.badwallet.shared.exceptions.SoldeInsuffisantException;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.TransactionType;
import edu.ism.badwallet.transaction.observer.TransactionPublisher;
import edu.ism.badwallet.transaction.operation.AbstractWalletOperation;
import edu.ism.badwallet.wallet.Wallet;
import edu.ism.badwallet.wallet.WalletRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Operation de retrait : sous-classe concrete du Template Method AbstractWalletOperation.
 */
public class WithdrawOperation extends AbstractWalletOperation<Transaction> {

    private static final BigDecimal FEE_RATE = new BigDecimal("0.01");
    private static final BigDecimal FEE_CAP = new BigDecimal("5000");

    private final Wallet wallet;
    private final BigDecimal amount;
    private final BigDecimal fees;
    private final WalletRepository walletRepository;

    public WithdrawOperation(Wallet wallet,
                             BigDecimal amount,
                             WalletRepository walletRepository,
                             TransactionPublisher publisher) {
        super(publisher);
        this.wallet = wallet;
        this.amount = amount;
        this.walletRepository = walletRepository;
        this.fees = computeFees(amount);
    }

    // Frais = min(montant * 1%, plafond), arrondi a l'entier XOF (scale 0, HALF_UP).
    private static BigDecimal computeFees(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(FEE_RATE)
                .min(FEE_CAP)
                .setScale(0, RoundingMode.HALF_UP);
    }

    @Override
    protected void validate() {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Le montant du retrait doit etre strictement positif");
        }
    }

    @Override
    protected void checkConstraints() {
        BigDecimal totalDebit = amount.add(fees);
        if (totalDebit.compareTo(wallet.getBalance()) > 0) {
            throw new SoldeInsuffisantException(
                    "Solde insuffisant : le retrait de " + amount + " majore des frais " + fees
                            + " depasse le solde disponible " + wallet.getBalance());
        }
    }

    @Override
    protected void applyToBalance() {
        wallet.setBalance(wallet.getBalance().subtract(amount.add(fees)));
        walletRepository.save(wallet);
    }

    @Override
    protected List<Transaction> buildTransactions() {
        return List.of(Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.RETRAIT)
                .amount(amount)
                .fees(fees)
                .paymentMethod(null)
                .build());
    }

    @Override
    protected Transaction buildResult(List<Transaction> transactions) {
        return transactions.get(0);
    }
}
