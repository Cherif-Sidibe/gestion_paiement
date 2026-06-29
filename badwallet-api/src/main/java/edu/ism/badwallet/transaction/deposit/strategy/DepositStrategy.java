package edu.ism.badwallet.transaction.deposit.strategy;

import edu.ism.badwallet.transaction.PaymentMethod;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.wallet.Wallet;

import java.math.BigDecimal;

/**
 * Strategy.
 * Construit la Transaction d'un depot selon la methode de paiement.
 */
public interface DepositStrategy {

    // Methode de paiement couverte par cette strategie.
    PaymentMethod method();

    // Construit la Transaction de depot (type DEPOT, fees 0, paymentMethod renseigne).
    Transaction buildDeposit(Wallet wallet, BigDecimal amount);
}
