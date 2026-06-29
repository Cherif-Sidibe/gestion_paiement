package edu.ism.badwallet.transaction.history;

import edu.ism.badwallet.client.web.transaction.TransactionMapper;
import edu.ism.badwallet.client.web.transaction.dto.TransactionResponseDto;
import edu.ism.badwallet.shared.exceptions.EntityNotFoundException;
import edu.ism.badwallet.transaction.TransactionRepository;
import edu.ism.badwallet.wallet.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionHistoryServiceImpl(WalletRepository walletRepository,
                                         TransactionRepository transactionRepository,
                                         TransactionMapper transactionMapper) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getTransactionsByPhone(String phone) {
        if (!walletRepository.existsByPhoneNumber(phone)) {
            throw new EntityNotFoundException("Wallet introuvable pour le numero " + phone);
        }
        return transactionRepository.findByWallet_PhoneNumberOrderByCreatedAtDesc(phone)
                .stream()
                .map(transactionMapper::toTransactionDto)
                .toList();
    }
}
