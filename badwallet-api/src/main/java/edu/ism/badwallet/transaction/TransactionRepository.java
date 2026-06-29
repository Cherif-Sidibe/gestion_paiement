package edu.ism.badwallet.transaction;

import edu.ism.badwallet.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Historique d'une wallet (par entite), le plus recent d'abord.
    List<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);

    // Historique d'une wallet par numero de telephone, le plus recent d'abord.
    List<Transaction> findByWallet_PhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}
