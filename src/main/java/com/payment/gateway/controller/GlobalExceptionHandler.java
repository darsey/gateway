package com.payment.gateway.controller;

import com.payment.gateway.model.PaymentResponse;
import com.payment.gateway.model.Status;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Hidden
@ControllerAdvice(basePackages = {"com.payment.gateway.controller"})
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PaymentResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
        });

        PaymentResponse errorResponse = PaymentResponse.builder().withTransactionId("")
                .withStatus(Status.DENIED)
                .withMessage(errorMessage.toString()).build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PaymentResponse> handleGenericExceptions(Exception ex, HttpServletRequest request)
            throws Exception {
        if (isSwaggerEndpoint(request)) {
            throw ex;
        }

        PaymentResponse errorResponse = PaymentResponse.builder().withTransactionId("")
                .withStatus(Status.DENIED)
                .withMessage("An error occurred: " + ex.getMessage()).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // Utility method to detect Swagger endpoints
    private boolean isSwaggerEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-ui");
    }
}