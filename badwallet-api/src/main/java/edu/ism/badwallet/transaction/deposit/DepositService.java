package edu.ism.badwallet.transaction.deposit;

import edu.ism.badwallet.client.web.transaction.dto.DepositRequestDto;
import edu.ism.badwallet.transaction.Transaction;

public interface DepositService {

    // Effectue un depot sur la wallet identifiee par son id ; retourne la Transaction creditee.
    Transaction deposit(Long walletId, DepositRequestDto request);
}
