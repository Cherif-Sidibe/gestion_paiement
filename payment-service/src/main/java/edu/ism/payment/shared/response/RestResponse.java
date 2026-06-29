package edu.ism.payment.shared.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public record RestResponse<T>(boolean success, HttpStatus status, String message, T body, LocalDateTime timestamp) {

    public RestResponse(boolean success, HttpStatus status, String message, T body)
    {
        this(success, status, message, body, LocalDateTime.now());
    }

    public static <T> RestResponse<T> success(T data, HttpStatus status)
    {
        return new RestResponse<>(true, status, "Données récupérées avec succès", data, LocalDateTime.now());
    }

    public static <T> RestResponse<T> error(String message, HttpStatus status)
    {
        return new RestResponse<>(false, status, message, null, LocalDateTime.now());
    }

    public static <T> RestResponse<T> error(String message, T data)
    {
        return new RestResponse<>(false, HttpStatus.BAD_REQUEST, message, data, LocalDateTime.now());
    }
}
