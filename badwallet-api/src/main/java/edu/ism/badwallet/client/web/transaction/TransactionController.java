package edu.ism.badwallet.client.web.transaction;

import edu.ism.badwallet.client.web.transaction.dto.DepositRequestDto;
import edu.ism.badwallet.client.web.transaction.dto.DepositResponseDto;
import edu.ism.badwallet.shared.response.RestResponse;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.deposit.DepositService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller des transactions d'une wallet.
 */
@RestController
@RequestMapping("/api/wallets")
public class TransactionController {

    private final DepositService depositService;
    private final TransactionMapper transactionMapper;

    public TransactionController(DepositService depositService, TransactionMapper transactionMapper) {
        this.depositService = depositService;
        this.transactionMapper = transactionMapper;
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<RestResponse<DepositResponseDto>> deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositRequestDto request) {
        Transaction tx = depositService.deposit(id, request);
        DepositResponseDto dto = transactionMapper.toDepositDto(tx);
        return ResponseEntity.status(HttpStatus.OK).body(RestResponse.success(dto, HttpStatus.OK));
    }
}
