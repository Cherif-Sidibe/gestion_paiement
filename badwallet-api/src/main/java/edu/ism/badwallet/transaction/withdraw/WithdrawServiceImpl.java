package edu.ism.badwallet.transaction.withdraw;

import edu.ism.badwallet.client.web.transaction.dto.WithdrawRequestDto;
import edu.ism.badwallet.shared.exceptions.EntityNotFoundException;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.observer.TransactionPublisher;
import edu.ism.badwallet.wallet.Wallet;
import edu.ism.badwallet.wallet.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WithdrawServiceImpl implements WithdrawService {

    private final WalletRepository walletRepository;
    private final TransactionPublisher publisher;

    public WithdrawServiceImpl(WalletRepository walletRepository,
                               TransactionPublisher publisher) {
        this.walletRepository = walletRepository;
        this.publisher = publisher;
    }

    @Override
    @Transactional
    public Transaction withdraw(WithdrawRequestDto request) {
        Wallet wallet = walletRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet introuvable pour le numero " + request.phoneNumber()));

        // Template Method
        WithdrawOperation operation = new WithdrawOperation(
                wallet, request.amount(), walletRepository, publisher);
        return operation.execute();
    }
}
