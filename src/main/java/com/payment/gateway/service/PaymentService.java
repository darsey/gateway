package com.payment.gateway.service;

import static com.payment.gateway.util.Constants.ACQUIRER_A;
import static com.payment.gateway.util.Constants.ACQUIRER_B;

import com.payment.gateway.model.PaymentRequest;
import com.payment.gateway.model.PaymentResponse;
import com.payment.gateway.model.Status;
import com.payment.gateway.util.CardUtils;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    private final Duration timeoutDuration;
    private final int latencyInMillis;

    //done like this for testing purposes, should be extracted into properties file
    public PaymentService() {
        this.latencyInMillis = 500; // Default value
        this.timeoutDuration = Duration.ofSeconds(5); // Default value
    }

    public PaymentService(int latencyInMillis, Duration timeoutDuration) {
        this.timeoutDuration = timeoutDuration;
        this.latencyInMillis = latencyInMillis;
    }

    private final ConcurrentHashMap<String, PaymentResponse> transactionStore = new ConcurrentHashMap<>();

    public Mono<PaymentResponse> processPayment(PaymentRequest request) {
        String transactionId = UUID.randomUUID().toString();

        return Mono.just(request)
                .flatMap(this::validateCard)
                .flatMap(validRequest -> initializePendingTransaction(validRequest, transactionId))
                .flatMap(validRequest -> routeToAcquirer(validRequest, transactionId))
                .flatMap(this::storeTransaction);
    }

    private Mono<PaymentRequest> initializePendingTransaction(PaymentRequest request, String transactionId) {
        PaymentResponse pendingResponse = PaymentResponse.builder()
                .withTransactionId(transactionId)
                .withStatus(Status.PENDING)
                .withMessage("Transaction is pending")
                .build();
        transactionStore.put(transactionId, pendingResponse);
        return Mono.just(request);
    }

    private Mono<PaymentRequest> validateCard(PaymentRequest request) {
        if (!CardUtils.isValidLuhn(request.getCardNumber())) {
            return Mono.error(new IllegalArgumentException("Invalid card number"));
        }
        return Mono.just(request);
    }

    private Mono<PaymentResponse> routeToAcquirer(PaymentRequest request, String transactionId) {
        String bin = CardUtils.getBin(request.getCardNumber());
        String acquirer = getAcquirer(bin);

        // Simulate acquirer response with latency, for timeout maybe add different status
        return Mono.delay(Duration.ofMillis(latencyInMillis))
                .map(ignored ->
                        PaymentResponse.builder().withTransactionId(transactionId)
                                .withStatus(getStatus(request))
                                .withMessage("Processed by " + acquirer).build())
                .timeout(timeoutDuration)
                .onErrorResume(TimeoutException.class, e ->
                        Mono.just(
                                PaymentResponse.builder().withTransactionId(transactionId)
                                        .withStatus(Status.DENIED)
                                        .withMessage("No response from " + acquirer)
                                        .build()));
    }

    private static Status getStatus(PaymentRequest request) {
        char lastDigit = CardUtils.getLastDigit(request.getCardNumber());
        return Status.forLastDigit(lastDigit);
    }


    private static String getAcquirer(String bin) {
        //this can be implemented as strategy pattern in case we have different services for each acquirer
        return CardUtils.isEvenSumOfDigits(bin) ? ACQUIRER_A : ACQUIRER_B;
    }


    private Mono<PaymentResponse> storeTransaction(PaymentResponse response) {
        transactionStore.put(response.getTransactionId(), response);
        return Mono.just(response);
    }
}
