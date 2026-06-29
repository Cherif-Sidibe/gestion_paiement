package edu.ism.badwallet.wallet;

import edu.ism.badwallet.client.web.wallet.dto.WalletCreateRequestDto;
import edu.ism.badwallet.client.web.wallet.dto.WalletCreateResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WalletService {
    WalletCreateResponseDto createWallet(WalletCreateRequestDto walletCreateRequestDto);

    Page<Wallet> findAllWallets(Pageable pageable);
}
