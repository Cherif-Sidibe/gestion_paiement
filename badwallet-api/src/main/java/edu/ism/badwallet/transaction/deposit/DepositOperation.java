package edu.ism.badwallet.transaction.deposit;

import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.deposit.strategy.DepositStrategy;
import edu.ism.badwallet.transaction.observer.TransactionPublisher;
import edu.ism.badwallet.transaction.operation.AbstractWalletOperation;
import edu.ism.badwallet.wallet.Wallet;
import edu.ism.badwallet.wallet.WalletRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Operation de depot : sous-classe concrete du Template Method AbstractWalletOperation.
 */
public class DepositOperation extends AbstractWalletOperation<Transaction> {

    private final Wallet wallet;
    private final BigDecimal amount;
    private final DepositStrategy strategy;
    private final WalletRepository walletRepository;

    public DepositOperation(Wallet wallet,
                            BigDecimal amount,
                            DepositStrategy strategy,
                            WalletRepository walletRepository,
                            TransactionPublisher publisher) {
        super(publisher);
        this.wallet = wallet;
        this.amount = amount;
        this.strategy = strategy;
        this.walletRepository = walletRepository;
    }

    @Override
    protected void validate() {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Le montant du depot doit etre strictement positif");
        }
    }

    @Override
    protected void applyToBalance() {
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    @Override
    protected List<Transaction> buildTransactions() {
        // La Strategy fixe le tag paymentMethod ; un depot = une seule ligne.
        return List.of(strategy.buildDeposit(wallet, amount));
    }

    @Override
    protected Transaction buildResult(List<Transaction> transactions) {
        return transactions.get(0);
    }
}
