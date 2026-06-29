package edu.ism.badwallet.transaction.observer;

import edu.ism.badwallet.transaction.Transaction;

/**
 * Observer (interface Observer).
 * Reagit a la survenue d'une transaction.
 */
public interface TransactionObserver {
    void onTransaction(Transaction tx);
}
