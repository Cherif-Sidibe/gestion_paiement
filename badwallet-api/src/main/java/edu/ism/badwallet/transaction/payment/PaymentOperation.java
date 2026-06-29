package edu.ism.badwallet.transaction.payment;

import edu.ism.badwallet.shared.exceptions.SoldeInsuffisantException;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.TransactionType;
import edu.ism.badwallet.transaction.observer.TransactionPublisher;
import edu.ism.badwallet.transaction.operation.AbstractWalletOperation;
import edu.ism.badwallet.wallet.Wallet;
import edu.ism.badwallet.wallet.WalletRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Operation de paiement de facture(s) : sous-classe concrete du Template Method
 * AbstractWalletOperation. Debite le montant total des factures ciblees et
 * historise une transaction PAIEMENT (sans frais). Le marquage des factures cote
 * payment-service est orchestre APRES par le service appelant, dans la meme
 * transaction, pour garantir le rollback du debit en cas d'echec distant.
 */
public class PaymentOperation extends AbstractWalletOperation<Transaction> {

    private final Wallet wallet;
    private final BigDecimal total;
    private final WalletRepository walletRepository;

    public PaymentOperation(Wallet wallet,
                            BigDecimal total,
                            WalletRepository walletRepository,
                            TransactionPublisher publisher) {
        super(publisher);
        this.wallet = wallet;
        this.total = total;
        this.walletRepository = walletRepository;
    }

    @Override
    protected void validate() {
        if (total == null || total.signum() <= 0) {
            throw new IllegalArgumentException("Le montant du paiement doit etre strictement positif");
        }
    }

    @Override
    protected void checkConstraints() {
        if (total.compareTo(wallet.getBalance()) > 0) {
            throw new SoldeInsuffisantException(
                    "Solde insuffisant : le paiement de " + total
                            + " depasse le solde disponible " + wallet.getBalance());
        }
    }

    @Override
    protected void applyToBalance() {
        wallet.setBalance(wallet.getBalance().subtract(total));
        walletRepository.save(wallet);
    }

    @Override
    protected List<Transaction> buildTransactions() {
        return List.of(Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.PAIEMENT)
                .amount(total)
                .fees(BigDecimal.ZERO)
                .paymentMethod(null)
                .build());
    }

    @Override
    protected Transaction buildResult(List<Transaction> transactions) {
        return transactions.get(0);
    }
}
