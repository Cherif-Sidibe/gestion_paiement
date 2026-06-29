package edu.ism.badwallet.transaction.deposit;

import edu.ism.badwallet.client.web.transaction.dto.DepositRequestDto;
import edu.ism.badwallet.shared.exceptions.EntityNotFoundException;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.deposit.strategy.DepositStrategy;
import edu.ism.badwallet.transaction.deposit.strategy.DepositStrategyFactory;
import edu.ism.badwallet.transaction.observer.TransactionPublisher;
import edu.ism.badwallet.wallet.Wallet;
import edu.ism.badwallet.wallet.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepositServiceImpl implements DepositService {

    private final WalletRepository walletRepository;
    private final DepositStrategyFactory strategyFactory;
    private final TransactionPublisher publisher;

    public DepositServiceImpl(WalletRepository walletRepository,
                              DepositStrategyFactory strategyFactory,
                              TransactionPublisher publisher) {
        this.walletRepository = walletRepository;
        this.strategyFactory = strategyFactory;
        this.publisher = publisher;
    }

    @Override
    @Transactional
    public Transaction deposit(Long walletId, DepositRequestDto request) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet introuvable pour l'id " + walletId));

        // Factory
        DepositStrategy strategy = strategyFactory.getStrategy(request.paymentMethod());

        // Template Method
        DepositOperation operation = new DepositOperation(
                wallet, request.amount(), strategy, walletRepository, publisher);
        return operation.execute();
    }
}
