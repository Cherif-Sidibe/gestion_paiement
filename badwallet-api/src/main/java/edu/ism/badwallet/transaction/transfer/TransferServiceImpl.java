package edu.ism.badwallet.transaction.transfer;

import edu.ism.badwallet.client.web.transaction.dto.TransferRequestDto;
import edu.ism.badwallet.shared.exceptions.EntityNotFoundException;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.observer.TransactionPublisher;
import edu.ism.badwallet.wallet.Wallet;
import edu.ism.badwallet.wallet.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransferServiceImpl implements TransferService {

    private final WalletRepository walletRepository;
    private final TransactionPublisher publisher;

    public TransferServiceImpl(WalletRepository walletRepository,
                               TransactionPublisher publisher) {
        this.walletRepository = walletRepository;
        this.publisher = publisher;
    }

    @Override
    @Transactional
    public List<Transaction> transfer(TransferRequestDto request) {
        Wallet sender = walletRepository.findByPhoneNumber(request.senderPhone())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet emettrice introuvable pour le numero " + request.senderPhone()));
        Wallet receiver = walletRepository.findByPhoneNumber(request.receiverPhone())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet receptrice introuvable pour le numero " + request.receiverPhone()));

        // Template Method
        TransferOperation operation = new TransferOperation(
                sender, receiver, request.amount(), walletRepository, publisher);
        return operation.execute();
    }
}
