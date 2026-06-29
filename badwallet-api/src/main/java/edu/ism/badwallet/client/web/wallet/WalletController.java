package edu.ism.badwallet.client.web.wallet;

import edu.ism.badwallet.client.web.wallet.dto.WalletCreateRequestDto;
import edu.ism.badwallet.client.web.wallet.dto.WalletCreateResponseDto;
import edu.ism.badwallet.shared.response.RestResponse;
import edu.ism.badwallet.wallet.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<RestResponse<WalletCreateResponseDto>> createWallet(
            @Valid @RequestBody WalletCreateRequestDto walletCreateRequestDto) {
        WalletCreateResponseDto walletCreateResponseDto = walletService.createWallet(walletCreateRequestDto);
        return new ResponseEntity<>(
                RestResponse.success(walletCreateResponseDto, HttpStatus.CREATED), HttpStatus.CREATED);
    }
}
