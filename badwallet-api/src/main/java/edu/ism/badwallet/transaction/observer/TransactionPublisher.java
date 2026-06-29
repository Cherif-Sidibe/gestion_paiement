package edu.ism.badwallet.transaction.observer;

import edu.ism.badwallet.transaction.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Observer (Subject).
 * Detient la liste des observers (implementations de TransactionObserver
 * injectees par Spring) et expose publish() qui notifie chacun.
 */
@Component
public class TransactionPublisher {

    private final List<TransactionObserver> observers;

    public TransactionPublisher(List<TransactionObserver> observers) {
        this.observers = observers;
    }

    public void publish(Transaction tx) {
        for (TransactionObserver observer : observers) {
            observer.onTransaction(tx);
        }
    }
}
