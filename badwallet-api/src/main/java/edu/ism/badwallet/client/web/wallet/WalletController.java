package edu.ism.badwallet.client.web.wallet;

import edu.ism.badwallet.client.web.wallet.dto.WalletCreateRequestDto;
import edu.ism.badwallet.client.web.wallet.dto.WalletCreateResponseDto;
import edu.ism.badwallet.client.web.wallet.dto.WalletResponseDto;
import edu.ism.badwallet.shared.response.PageResponse;
import edu.ism.badwallet.shared.response.RestResponse;
import edu.ism.badwallet.wallet.Wallet;
import edu.ism.badwallet.wallet.WalletService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    private final WalletService walletService;
    private final WalletMapper walletMapper;

    public WalletController(WalletService walletService, WalletMapper walletMapper) {
        this.walletService = walletService;
        this.walletMapper = walletMapper;
    }

    @PostMapping
    public ResponseEntity<RestResponse<WalletCreateResponseDto>> createWallet(
            @Valid @RequestBody WalletCreateRequestDto walletCreateRequestDto) {
        WalletCreateResponseDto walletCreateResponseDto = walletService.createWallet(walletCreateRequestDto);
        return new ResponseEntity<>(
                RestResponse.success(walletCreateResponseDto, HttpStatus.CREATED), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponse<WalletResponseDto>> getAllWallets(
            @RequestParam(defaultValue = "${api.pagination.default-page}") int page,
            @RequestParam(defaultValue = "${api.pagination.default-size}") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Wallet> wallets = walletService.findAllWallets(pageable);

        Page<WalletResponseDto> dtoPage = wallets.map(walletMapper::toDto);
        return ResponseEntity.status(HttpStatus.OK).body(PageResponse.fromPage(dtoPage));
    }
}
