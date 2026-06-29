package edu.ism.badwallet.client.web.payment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ism.badwallet.client.web.payment.dto.PayFacturesRequestDto;
import edu.ism.badwallet.client.web.payment.dto.PayFacturesResponseDto;
import edu.ism.badwallet.client.web.payment.dto.PayRequestDto;
import edu.ism.badwallet.client.web.payment.dto.PayResponseDto;
import edu.ism.badwallet.shared.response.RestResponse;
import edu.ism.badwallet.transaction.payment.PaymentService;
import jakarta.validation.Valid;

/**
 * Paiement de factures depuis une wallet : debite la wallet puis relaie le
 * marquage des factures payees vers payment-service.
 */
@RestController
@RequestMapping("/api/wallets")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Paie la facture du mois courant pour ce service (relai pay-current).
    @PostMapping("/pay")
    public ResponseEntity<RestResponse<PayResponseDto>> pay(
            @Valid @RequestBody PayRequestDto request) {
        PayResponseDto dto = paymentService.pay(request);
        return ResponseEntity.status(HttpStatus.OK).body(RestResponse.success(dto, HttpStatus.OK));
    }

    // Paie les factures listees par reference (relai pay-by-references).
    @PostMapping("/pay-factures")
    public ResponseEntity<RestResponse<PayFacturesResponseDto>> payFactures(
            @Valid @RequestBody PayFacturesRequestDto request) {
        PayFacturesResponseDto dto = paymentService.payFactures(request);
        return ResponseEntity.status(HttpStatus.OK).body(RestResponse.success(dto, HttpStatus.OK));
    }
}
