package edu.ism.badwallet.transaction.operation;

import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.observer.TransactionPublisher;

import java.util.List;

/**
 * Template Method.
 * Definit le squelette invariant (execute(), final) d'une operation d'argent.
 * Les sous-classes redefinissent les etapes qui les concernent ; l'ordre et le
 * branchement sur l'Observer restent figes ici.
 *
 * @param <R> type du resultat retourne par l'operation.
 */
public abstract class AbstractWalletOperation<R> {

    protected final TransactionPublisher publisher;

    protected AbstractWalletOperation(TransactionPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Methode template : enchaine les 5 etapes dans un ordre fige.
     * final = les sous-classes ne peuvent pas casser le squelette.
     */
    public final R execute() {
        validate();                                  // 1. controles d'entree
        checkConstraints();                          // 2. contraintes metier (ex. solde)
        applyToBalance();                            // 3. mise a jour du/des solde(s)
        List<Transaction> transactions = buildTransactions(); // 4. construction de la/les transaction(s)
        notifyObservers(transactions);               // 5. publication -> Observer (persistance historique)
        return buildResult(transactions);
    }

    /** Etape 1 - hook : controles d'entree. Vide par defaut. */
    protected void validate() {
    }

    /** Etape 2 - hook : contraintes metier. Vide par defaut. */
    protected void checkConstraints() {
    }

    /** Etape 3 - applique l'effet sur le(s) solde(s) de la/les wallet(s). */
    protected abstract void applyToBalance();

    /** Etape 4 - construit la/les Transaction(s) a historiser. */
    protected abstract List<Transaction> buildTransactions();

    /**
     * Etape 5 - etape concrete partagee : publie chaque transaction au Subject,
     * qui la transmet aux observers (l'observer d'historique la persiste).
     */
    protected final void notifyObservers(List<Transaction> transactions) {
        for (Transaction tx : transactions) {
            publisher.publish(tx);
        }
    }

    /** Construit le resultat retourne par execute() a partir de la/les transaction(s). */
    protected abstract R buildResult(List<Transaction> transactions);
}
