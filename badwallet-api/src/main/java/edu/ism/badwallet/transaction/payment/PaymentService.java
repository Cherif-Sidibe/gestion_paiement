package edu.ism.badwallet.transaction.payment;

import edu.ism.badwallet.client.web.payment.dto.PayFacturesRequestDto;
import edu.ism.badwallet.client.web.payment.dto.PayFacturesResponseDto;
import edu.ism.badwallet.client.web.payment.dto.PayRequestDto;
import edu.ism.badwallet.client.web.payment.dto.PayResponseDto;

public interface PaymentService {

    PayResponseDto pay(PayRequestDto request);

    PayFacturesResponseDto payFactures(PayFacturesRequestDto request);
}
