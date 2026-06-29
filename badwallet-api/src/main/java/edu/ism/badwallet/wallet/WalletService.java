package edu.ism.badwallet.wallet;

import edu.ism.badwallet.client.web.wallet.dto.WalletCreateRequestDto;
import edu.ism.badwallet.client.web.wallet.dto.WalletCreateResponseDto;

public interface WalletService {
    WalletCreateResponseDto createWallet(WalletCreateRequestDto walletCreateRequestDto);
}
