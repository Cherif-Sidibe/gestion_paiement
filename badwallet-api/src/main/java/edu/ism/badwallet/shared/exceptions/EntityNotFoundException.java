package edu.ism.badwallet.shared.exceptions;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String message)
    {
        super(message);
    }
}
