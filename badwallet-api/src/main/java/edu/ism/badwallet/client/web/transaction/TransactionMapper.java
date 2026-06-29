package edu.ism.badwallet.client.web.transaction;

import edu.ism.badwallet.client.web.transaction.dto.DepositResponseDto;
import edu.ism.badwallet.client.web.transaction.dto.TransferResponseDto;
import edu.ism.badwallet.client.web.transaction.dto.WithdrawResponseDto;
import edu.ism.badwallet.shared.mapper.DateMapper;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.wallet.Wallet;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapping manuel Transaction -> DTO.
 */
@Component
public class TransactionMapper {

    private final DateMapper dateMapper;

    public TransactionMapper(DateMapper dateMapper) {
        this.dateMapper = dateMapper;
    }

    public DepositResponseDto toDepositDto(Transaction tx) {
        if (tx == null) {
            return null;
        }
        Wallet wallet = tx.getWallet();
        return DepositResponseDto.builder()
                .walletId(wallet != null ? wallet.getId() : null)
                .code(wallet != null ? wallet.getCode() : null)
                .phoneNumber(wallet != null ? wallet.getPhoneNumber() : null)
                .currency(wallet != null ? wallet.getCurrency() : null)
                .amount(tx.getAmount())
                .newBalance(wallet != null ? wallet.getBalance() : null)
                .type(tx.getType() != null ? tx.getType().name() : null)
                .paymentMethod(tx.getPaymentMethod() != null ? tx.getPaymentMethod().name() : null)
                .createdAt(dateMapper.formatLocalDateTime(tx.getCreatedAt(), "dd/MM/yyyy HH:mm:ss"))
                .build();
    }

    public WithdrawResponseDto toWithdrawDto(Transaction tx) {
        if (tx == null) {
            return null;
        }
        Wallet wallet = tx.getWallet();
        return WithdrawResponseDto.builder()
                .walletId(wallet != null ? wallet.getId() : null)
                .code(wallet != null ? wallet.getCode() : null)
                .phoneNumber(wallet != null ? wallet.getPhoneNumber() : null)
                .currency(wallet != null ? wallet.getCurrency() : null)
                .amount(tx.getAmount())
                .fees(tx.getFees())
                .newBalance(wallet != null ? wallet.getBalance() : null)
                .type(tx.getType() != null ? tx.getType().name() : null)
                .paymentMethod(tx.getPaymentMethod() != null ? tx.getPaymentMethod().name() : null)
                .createdAt(dateMapper.formatLocalDateTime(tx.getCreatedAt(), "dd/MM/yyyy HH:mm:ss"))
                .build();
    }

    // Les deux transactions du transfert : [0] TRANSFERT_ENVOYE (emetteur), [1] TRANSFERT_RECU (recepteur).
    public TransferResponseDto toTransferDto(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return null;
        }
        Transaction sent = transactions.get(0);
        Transaction received = transactions.size() > 1 ? transactions.get(1) : null;
        Wallet sender = sent.getWallet();
        Wallet receiver = received != null ? received.getWallet() : null;
        return TransferResponseDto.builder()
                .senderPhone(sender != null ? sender.getPhoneNumber() : null)
                .receiverPhone(receiver != null ? receiver.getPhoneNumber() : null)
                .currency(sender != null ? sender.getCurrency() : null)
                .amount(sent.getAmount())
                .fees(sent.getFees())
                .newSenderBalance(sender != null ? sender.getBalance() : null)
                .type(sent.getType() != null ? sent.getType().name() : null)
                .createdAt(dateMapper.formatLocalDateTime(sent.getCreatedAt(), "dd/MM/yyyy HH:mm:ss"))
                .build();
    }
}
