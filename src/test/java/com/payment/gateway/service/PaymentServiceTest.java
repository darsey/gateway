package com.payment.gateway.service;

import com.payment.gateway.model.PaymentRequest;
import com.payment.gateway.model.PaymentResponse;
import com.payment.gateway.model.Status;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();
    }

    @Test
    void processPayment_ValidRequest_ShouldReturnApprovedResponse() {
        PaymentRequest request = new PaymentRequest(
                "4137894711755904", "12/25", "123", 100.0, "USD", "merchant123"
        );

        Mono<PaymentResponse> responseMono = paymentService.processPayment(request);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getStatus() == Status.APPROVED)
                .verifyComplete();
    }

    @Test
    void processPayment_InvalidCard_ShouldReturnError() {
        PaymentRequest request = new PaymentRequest(
                "1234567890123450", "12/25", "123", 100.0, "USD", "merchant123"
        );

        Mono<PaymentResponse> responseMono = paymentService.processPayment(request);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Invalid card number"))
                .verify();
    }

    @Test
    void processPayment_AcquirerTimeout_ShouldExpectTimeout() {
        // Simulate timeout scenario
        PaymentService service = new PaymentService(
                6000, // Latency of 6 seconds (exceeds timeout)
                Duration.ofSeconds(5) // Timeout after 5 seconds
        );

        PaymentRequest request = new PaymentRequest(
                "4137894711755904", "12/25", "123", 100.0, "USD", "merchant123"
        );

        Mono<PaymentResponse> responseMono = service.processPayment(request);

        StepVerifier.create(responseMono)
                .expectTimeout(Duration.ofSeconds(5))
                .verify();
    }

    @Test
    void processPayment_AcquirerTimeout_ShouldReturnDeniedResponse() {
        // Simulate timeout scenario
        PaymentService service = new PaymentService(
                6000, // Simulated latency (6 seconds, longer than timeout)
                Duration.ofSeconds(5) // Timeout after 5 seconds
        );

        PaymentRequest request = new PaymentRequest(
                "4137894711755904", "12/25", "123", 100.0, "USD", "merchant123"
        );

        Mono<PaymentResponse> responseMono = service.processPayment(request);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getStatus() == Status.DENIED &&
                        response.getMessage().startsWith("No response from Acquirer A"))
                .verifyComplete();
    }
}