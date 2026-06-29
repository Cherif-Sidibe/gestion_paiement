package edu.ism.badwallet.shared.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import edu.ism.badwallet.shared.response.RestResponse;
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

    @ExceptionHandler(SoldeInsuffisantException.class)
    public ResponseEntity<RestResponse> handleSoldeInsuffisantException(SoldeInsuffisantException ex) {
        return ResponseEntity.status(400).body(RestResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(SameWalletTransferException.class)
    public ResponseEntity<RestResponse> handleSameWalletTransferException(SameWalletTransferException ex) {
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
