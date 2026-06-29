package edu.ism.payment.shared.exceptions;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String message)
    {
        super(message);
    }
}
