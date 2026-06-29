package edu.ism.badwallet.transaction.history;

import edu.ism.badwallet.client.web.transaction.dto.TransactionResponseDto;

import java.util.List;

public interface TransactionHistoryService {
    List<TransactionResponseDto> getTransactionsByPhone(String phone);
}
