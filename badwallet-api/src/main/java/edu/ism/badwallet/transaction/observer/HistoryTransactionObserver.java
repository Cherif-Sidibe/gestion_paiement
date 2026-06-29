package edu.ism.badwallet.transaction.observer;

import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.TransactionRepository;
import org.springframework.stereotype.Component;

/**
 * Observer (observer concret).
 * Persiste la transaction via le TransactionRepository.
 */
@Component
public class HistoryTransactionObserver implements TransactionObserver {

    private final TransactionRepository transactionRepository;

    public HistoryTransactionObserver(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void onTransaction(Transaction tx) {
        transactionRepository.save(tx);
    }
}
