package com.imperial_net.inventioryApp.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DisabledUserException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleDisabledUserException(DisabledUserException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(UserRegisterException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleUserRegisterException(UserRegisterException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(ProviderException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleProviderException(ProviderException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        // Extrae los mensajes de error personalizados definidos en la entidad
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorResponse.put("error", error.getDefaultMessage()) // Solo toma el primer error
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.BAD_REQUEST);
    }

}
