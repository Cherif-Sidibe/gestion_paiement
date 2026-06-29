package edu.ism.badwallet.transaction.deposit.strategy;

import edu.ism.badwallet.transaction.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Factory.
 * Retourne la DepositStrategy correspondant a la PaymentMethod. Spring injecte
 * toutes les implementations de DepositStrategy ; on les indexe par methode.
 */
@Component
public class DepositStrategyFactory {

    private final Map<PaymentMethod, DepositStrategy> strategies = new EnumMap<>(PaymentMethod.class);

    public DepositStrategyFactory(List<DepositStrategy> strategyList) {
        for (DepositStrategy strategy : strategyList) {
            strategies.put(strategy.method(), strategy);
        }
    }

    public DepositStrategy getStrategy(PaymentMethod method) {
        DepositStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("Aucune strategie de depot pour la methode " + method);
        }
        return strategy;
    }
}
