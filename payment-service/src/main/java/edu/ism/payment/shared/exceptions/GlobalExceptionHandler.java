package edu.ism.payment.shared.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import edu.ism.payment.shared.response.RestResponse;
import jakarta.persistence.EntityExistsException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RestResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(RestResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<RestResponse> handleEntityExistException(EntityExistsException ex) {
        return ResponseEntity.status(409).body(RestResponse.error(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(FactureNotFoundException.class)
    public ResponseEntity<RestResponse> handleFactureNotFoundException(FactureNotFoundException ex) {
        return ResponseEntity.status(404).body(RestResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(FactureAlreadyPaidException.class)
    public ResponseEntity<RestResponse> handleFactureAlreadyPaidException(FactureAlreadyPaidException ex) {
        return ResponseEntity.status(409).body(RestResponse.error(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(FactureNotForWalletException.class)
    public ResponseEntity<RestResponse> handleFactureNotForWalletException(FactureNotForWalletException ex) {
        return ResponseEntity.status(400).body(RestResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<RestResponse> handleInvalidAmountException(InvalidAmountException ex) {
        return ResponseEntity.status(400).body(RestResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(400).body(RestResponse.error("Données invalides", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse> handleGenericException(Exception ex) {
        return ResponseEntity.status(500)
                .body(RestResponse.error("Une erreur interne est survenue", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
