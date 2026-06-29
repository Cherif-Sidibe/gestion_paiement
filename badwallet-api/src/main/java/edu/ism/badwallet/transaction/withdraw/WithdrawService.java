package edu.ism.badwallet.transaction.withdraw;

import edu.ism.badwallet.client.web.transaction.dto.WithdrawRequestDto;
import edu.ism.badwallet.transaction.Transaction;

public interface WithdrawService {

    // Effectue un retrait sur la wallet identifiee par son numero de telephone ; retourne la Transaction de retrait.
    Transaction withdraw(WithdrawRequestDto request);
}
