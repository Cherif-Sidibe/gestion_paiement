package edu.ism.badwallet.shared.exceptions;

public class SameWalletTransferException extends RuntimeException {
    public SameWalletTransferException(String message) {
        super(message);
    }
}
