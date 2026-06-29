package edu.ism.badwallet.shared.exceptions;

public class PaymentServiceUnavailableException extends RuntimeException {
    public PaymentServiceUnavailableException(String message) {
        super(message);
    }
}
