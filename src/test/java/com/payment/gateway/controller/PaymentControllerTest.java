package com.payment.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.payment.gateway.model.PaymentRequest;
import com.payment.gateway.model.PaymentResponse;
import com.payment.gateway.model.Status;
import com.payment.gateway.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PaymentControllerTest {

    private PaymentService paymentService;
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        paymentController = new PaymentController(paymentService);
    }

    @Test
    void processPayment_ValidRequest_ShouldReturnApprovedResponse() {
        PaymentRequest request = new PaymentRequest(
                "1234567890123456", "12/25", "123", 100.0, "USD", "merchant123"
        );

        PaymentResponse mockResponse = new PaymentResponse("txn123", Status.APPROVED, "Processed by Acquirer A");
        when(paymentService.processPayment(any())).thenReturn(Mono.just(mockResponse));

        Mono<ResponseEntity<PaymentResponse>> responseMono = paymentController.processPayment(request);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getBody().getStatus() == Status.APPROVED)
                .verifyComplete();
    }

    @Test
    void processPayment_InvalidCard_ShouldReturnBadRequestResponse() {
        PaymentRequest request = new PaymentRequest(
                "1234567890123450", "12/25", "123", 100.0, "USD", "merchant123"
        );

        when(paymentService.processPayment(any())).thenReturn(
                Mono.error(new IllegalArgumentException("Invalid card number")));

        Mono<ResponseEntity<PaymentResponse>> responseMono = paymentController.processPayment(request);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.BAD_REQUEST &&
                        response.getBody().getStatus() == Status.DENIED)
                .verifyComplete();
    }
}