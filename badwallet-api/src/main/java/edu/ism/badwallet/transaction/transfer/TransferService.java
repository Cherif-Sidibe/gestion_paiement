package edu.ism.badwallet.transaction.transfer;

import edu.ism.badwallet.client.web.transaction.dto.TransferRequestDto;
import edu.ism.badwallet.transaction.Transaction;

import java.util.List;

public interface TransferService {

    // Transfere un montant de la wallet emettrice vers la wallet receptrice ; retourne les deux transactions (ENVOYE, RECU).
    List<Transaction> transfer(TransferRequestDto request);
}
