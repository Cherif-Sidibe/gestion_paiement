package edu.ism.payment.shared.exceptions;

// 400 : la facture ciblee n'appartient pas a la wallet indiquee (incoherence).
public class FactureNotForWalletException extends RuntimeException {
    public FactureNotForWalletException(String message) {
        super(message);
    }
}
