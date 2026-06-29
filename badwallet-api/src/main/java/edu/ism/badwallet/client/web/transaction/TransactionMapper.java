package edu.ism.badwallet.client.web.transaction;

import edu.ism.badwallet.client.web.transaction.dto.DepositResponseDto;
import edu.ism.badwallet.shared.mapper.DateMapper;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.wallet.Wallet;
import org.springframework.stereotype.Component;

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
}
