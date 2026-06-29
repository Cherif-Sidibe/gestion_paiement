package edu.ism.badwallet.transaction.transfer;

import edu.ism.badwallet.shared.exceptions.SameWalletTransferException;
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
 * Operation de transfert : sous-classe concrete du Template Method AbstractWalletOperation.
 * Porte DEUX wallets (emetteur + recepteur) et produit DEUX transactions sans frais.
 */
public class TransferOperation extends AbstractWalletOperation<List<Transaction>> {

    private final Wallet sender;
    private final Wallet receiver;
    private final BigDecimal amount;
    private final WalletRepository walletRepository;

    public TransferOperation(Wallet sender,
                             Wallet receiver,
                             BigDecimal amount,
                             WalletRepository walletRepository,
                             TransactionPublisher publisher) {
        super(publisher);
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.walletRepository = walletRepository;
    }

    @Override
    protected void validate() {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Le montant du transfert doit etre strictement positif");
        }
        if (sender.getPhoneNumber() != null
                && sender.getPhoneNumber().equals(receiver.getPhoneNumber())) {
            throw new SameWalletTransferException("Impossible de transferer vers le meme portefeuille");
        }
    }

    @Override
    protected void checkConstraints() {
        if (amount.compareTo(sender.getBalance()) > 0) {
            throw new SoldeInsuffisantException(
                    "Solde insuffisant : le transfert de " + amount
                            + " depasse le solde disponible " + sender.getBalance());
        }
    }

    @Override
    protected void applyToBalance() {
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        walletRepository.save(sender);
        walletRepository.save(receiver);
    }

    @Override
    protected List<Transaction> buildTransactions() {
        Transaction sent = Transaction.builder()
                .wallet(sender)
                .type(TransactionType.TRANSFERT_ENVOYE)
                .amount(amount)
                .fees(BigDecimal.ZERO)
                .paymentMethod(null)
                .build();
        Transaction received = Transaction.builder()
                .wallet(receiver)
                .type(TransactionType.TRANSFERT_RECU)
                .amount(amount)
                .fees(BigDecimal.ZERO)
                .paymentMethod(null)
                .build();
        return List.of(sent, received);
    }

    @Override
    protected List<Transaction> buildResult(List<Transaction> transactions) {
        return transactions;
    }
}
