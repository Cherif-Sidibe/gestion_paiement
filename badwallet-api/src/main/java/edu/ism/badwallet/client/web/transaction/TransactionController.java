package edu.ism.badwallet.client.web.transaction;

import edu.ism.badwallet.client.web.transaction.dto.DepositRequestDto;
import edu.ism.badwallet.client.web.transaction.dto.DepositResponseDto;
import edu.ism.badwallet.client.web.transaction.dto.TransferRequestDto;
import edu.ism.badwallet.client.web.transaction.dto.TransferResponseDto;
import edu.ism.badwallet.client.web.transaction.dto.WithdrawRequestDto;
import edu.ism.badwallet.client.web.transaction.dto.WithdrawResponseDto;
import edu.ism.badwallet.shared.response.RestResponse;
import edu.ism.badwallet.transaction.Transaction;
import edu.ism.badwallet.transaction.deposit.DepositService;
import edu.ism.badwallet.transaction.transfer.TransferService;
import edu.ism.badwallet.transaction.withdraw.WithdrawService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller des transactions d'une wallet.
 */
@RestController
@RequestMapping("/api/wallets")
public class TransactionController {

    private final DepositService depositService;
    private final WithdrawService withdrawService;
    private final TransferService transferService;
    private final TransactionMapper transactionMapper;

    public TransactionController(DepositService depositService,
                                 WithdrawService withdrawService,
                                 TransferService transferService,
                                 TransactionMapper transactionMapper) {
        this.depositService = depositService;
        this.withdrawService = withdrawService;
        this.transferService = transferService;
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

    @PostMapping("/withdraw")
    public ResponseEntity<RestResponse<WithdrawResponseDto>> withdraw(
            @Valid @RequestBody WithdrawRequestDto request) {
        Transaction tx = withdrawService.withdraw(request);
        WithdrawResponseDto dto = transactionMapper.toWithdrawDto(tx);
        return ResponseEntity.status(HttpStatus.OK).body(RestResponse.success(dto, HttpStatus.OK));
    }

    @PostMapping("/transfer")
    public ResponseEntity<RestResponse<TransferResponseDto>> transfer(
            @Valid @RequestBody TransferRequestDto request) {
        List<Transaction> transactions = transferService.transfer(request);
        TransferResponseDto dto = transactionMapper.toTransferDto(transactions);
        return ResponseEntity.status(HttpStatus.OK).body(RestResponse.success(dto, HttpStatus.OK));
    }
}
