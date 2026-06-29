package edu.ism.badwallet.transaction.deposit.strategy;

import edu.ism.badwallet.transaction.PaymentMethod;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.TransactionType;
import edu.ism.badwallet.wallet.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy (implementation CREDIT_CARD).
 * Construit une Transaction de depot taggue CREDIT_CARD.
 */
@Component
public class CreditCardDepositStrategy implements DepositStrategy {

    @Override
    public PaymentMethod method() {
        return PaymentMethod.CREDIT_CARD;
    }

    @Override
    public Transaction buildDeposit(Wallet wallet, BigDecimal amount) {
        return Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.DEPOT)
                .amount(amount)
                .fees(BigDecimal.ZERO)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();
    }
}
