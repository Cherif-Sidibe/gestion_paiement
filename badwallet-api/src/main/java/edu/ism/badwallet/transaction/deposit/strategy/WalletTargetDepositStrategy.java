package edu.ism.badwallet.transaction.deposit.strategy;

import edu.ism.badwallet.transaction.PaymentMethod;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.TransactionType;
import edu.ism.badwallet.wallet.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy (implementation WALLET_TARGET).
 * Construit une Transaction de depot taggue WALLET_TARGET.
 */
@Component
public class WalletTargetDepositStrategy implements DepositStrategy {

    @Override
    public PaymentMethod method() {
        return PaymentMethod.WALLET_TARGET;
    }

    @Override
    public Transaction buildDeposit(Wallet wallet, BigDecimal amount) {
        return Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.DEPOT)
                .amount(amount)
                .fees(BigDecimal.ZERO)
                .paymentMethod(PaymentMethod.WALLET_TARGET)
                .build();
    }
}
